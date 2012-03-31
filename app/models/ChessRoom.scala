package models

import akka.actor._
import akka.util.duration._

import play.api._
import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent._

import akka.util.Timeout
import akka.pattern.ask

import play.api.Play.current

import com.codahale.jerkson.Json

import chess.PieceType
import chess.PieceType._
import chess.Game
import chess.{Move, BasicMove, CastlingMove, EnPassantMove, PromotionMove}

import chess.codec.FENEncoder


object ChessRoom {
  
  var game = new Game()
  
  implicit val timeout = Timeout(1 second)
  
  lazy val default = {
    val roomActor = Akka.system.actorOf(Props[ChessRoom])
    
    roomActor
  }

  def fenEncodeGame = FENEncoder(game.currentPosition).encode  
  
  def encodeGame = {
    JsObject(
      Seq(
        "fen" -> JsString(fenEncodeGame),
        "movelist" -> JsArray(
          ChessRoom.game.currentPosition.moveList.filter {
            _ match {
              case PromotionMove(_, _, _, promoteTo) if promoteTo != Queen => false 
              case _ => true
            }
          } map { move =>
		    JsObject(
		      Seq(
		        "from" -> JsNumber(move.fromSquare),
		        "to" -> JsNumber(move.toSquare)
		      ) ++ {
		        move match {
                  case CastlingMove(f, _, _) => Seq("isCastling" -> JsBoolean(true))
				  case EnPassantMove(_, _, epSquare) => Seq("enPassantCaptureSquare" -> JsNumber(epSquare))
				  case PromotionMove(_, _, _, _) => Seq("isPromotion" -> JsBoolean(true))
				  case BasicMove(_, _, _) => Nil
				}
		      }
		    )
		  }
        )
      )
    )
  }
    
  def join(username:String):Promise[(Iteratee[JsValue,_],Enumerator[JsValue])] = {
    (default ? Join(username)).asPromise.map {
      
      case Connected(enumerator) => 
      
        // Create an Iteratee to consume the feed
        val iteratee = Iteratee.foreach[JsValue] { event =>
          val fromSquare = (event \ "from").as[Int]
          val toSquare = (event \ "to").as[Int]
          val promotionPiece = (event \ "promotionPiece").asOpt[String].flatMap { p: String =>
            Some(p(0)) flatMap { PieceType.fromChar(_) }
          }
          println("promotionPiece=" + promotionPiece)
          default ! MakeMove(username, fromSquare, toSquare, promotionPiece)
        }.mapDone { _ =>
          default ! Quit(username)
        }

        (iteratee,enumerator)
        
      case CannotConnect(error) => 
      
        // Connection error

        // A finished Iteratee sending EOF
        val iteratee = Done[JsValue,Unit]((),Input.EOF)

        // Send an error and close the socket
        val enumerator =  Enumerator[JsValue](JsObject(Seq("error" -> JsString(error)))).andThen(Enumerator.enumInput(Input.EOF))
        
        (iteratee,enumerator)
         
    }

  }
  
}

class ChessRoom extends Actor {
  
  var members = Map.empty[String, PushEnumerator[JsValue]]
  
  def receive = {
    
    case Join(username) => {
      // Create an Enumerator to write to this socket
      val channel =  Enumerator.imperative[JsValue]( onStart = self ! NotifyJoin(username))
      if(members.contains(username)) {
        sender ! CannotConnect("This username is already used")
      } else {
        members = members + (username -> channel)
        
        sender ! Connected(channel)
      }
    }

    case NotifyJoin(username) => {
      notifyAll("join", username, "has entered the room")
    }
    
    case MakeMove(username, fromSquare, toSquare, promotionPiece) => {
      Logger.info("ChessRoom.receive MakeMove fromSquare=" + fromSquare + " toSquare=" + toSquare + " promotionPiece=" + promotionPiece)
      ChessRoom.game = ChessRoom.game.makeMove(fromSquare.toInt, toSquare.toInt, promotionPiece).getOrElse(ChessRoom.game)
      notifyAll("game", username, "")
    }
    
    case Quit(username) => {
      members = members - username
      notifyAll("quit", username, "has leaved the room")
    }
    
  }
  
  def notifyAll(kind: String, user: String, text: String) {
    val msg = JsObject(
      Seq(
        "kind" -> JsString(kind),
        "user" -> JsString(user),
        "message" -> JsString(text),
        "game" -> ChessRoom.encodeGame,
        "members" -> JsArray(
          members.keySet.toList.map(JsString)
        )
      )
    )
    members.foreach { 
      case (_, channel) => channel.push(msg)
    }
  }
  
}

case class MakeMove(username: String, fromSquare: Int, toSquare: Int, promotionPiece: Option[PieceType.Value])
case class Join(username: String)
case class Quit(username: String)
case class NotifyJoin(username: String)

case class Connected(enumerator:Enumerator[JsValue])
case class CannotConnect(msg: String)