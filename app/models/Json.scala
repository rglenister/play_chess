package models


import scala.math.BigDecimal.int2bigDecimal
import chess.PieceColor.Black
import chess.PieceColor.White
import chess.PieceType.Queen
import chess.codec.FENEncoder
import chess.format.MoveFormatter
import chess.BasicMove
import chess.CastlingMove
import chess.EnPassantMove
import chess.Game
import chess.PieceColor
import chess.PieceColor._
import chess.PieceType
import chess.Player
import chess.PromotionMove
import play.api.libs.json.JsArray
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsNumber
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import chess.Move
import chess.GameMove


/**
 * Conversions from game to Json.
 */
object JsonGameSerializer {
  
  /**
   * Serializes the given game in the front end format
   * 
   * @param game is the game to serialize.
   * @return JsValue
   */
  def serializeFrontEnd(game: Game) = {
    JsObject(
      Seq(
        encodeFEN(game),
        serializePlayers(game),
        serializeFormattedMoveHistory(game),
        serializePositionIndex(game),
        serializeLegalMoves(game)
      )
    )
  }
  
  /**
   * Serializes the given game in the DB format.
   * 
   * @param game is the game to serialize.
   * @return a json encoded string.
   */
  def serializeDB(game: Game) = {
    JsObject(
      Seq(
        serializePlayers(game),
        serializeMoveHistory(game),
        serializePositionIndex(game)
      )
    ).toString
  }
  
  private def encodeFEN(game: Game) = "fen" -> JsString(FENEncoder(game.currentPosition).encode)
  
  private def serializePlayers(game: Game) = {
    "players" -> JsObject(
      Seq(
        "white" -> JsString(game.players(White).name),
        "black" -> JsString(game.players(Black).name)
      )
    )    
  }
  
  private def serializeFormattedMoveHistory(game: Game) = {
    "moveHistory" -> JsArray(
      MoveFormatter().formatMoves(game).map { JsString(_) } toList
    )    
  }
  
  private def serializeMoveHistory(game: Game) = {
    "moveHistory" -> JsArray(
      game.moveMap.values.toList.map { gameMove => 
        JsObject(
          Seq(
		    "from" -> JsString(gameMove.move.fromSquare.toString),
		    "to" -> JsString(gameMove.move.toSquare.toString)
		  ) ++ {
		    gameMove.move match {
		      case PromotionMove(_, _, _, promoteTo) => Seq("promoteTo" -> JsString(PieceType.toChar(promoteTo).toString))
			  case _ => Nil
			}
		  }
		)
      }
    )
  }
  
  private def serializePositionIndex(game: Game) = "positionIndex" -> JsNumber(game.nextMoveMapIndex)
  
  private def serializeLegalMoves(game: Game) = {
    "legalMoves" -> JsArray(
      game.currentPosition.moveList.filter {
        case PromotionMove(_, _, _, promoteTo) if promoteTo != Queen => false 
        case _ => true
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
  }
}

/**
 * Conversions from Json to game.
 */
object JsonGameParser {

  /**
   * Parses the given json DB formatted string.
   * 
   * @param jsonString is the json string.
   * @return a game.
   */
  def parseDB(jsonString: String): Game = {
    val json = Json.parse(jsonString)
    val whitePlayer = Player((json \ "players" \ "white").as[String])
    val blackPlayer = Player((json \ "players" \ "black").as[String])
    val moves = (json \ "moveHistory").as[List[Map[String, String]]]
    val positionIndex = (json \ "positionIndex").as[Int]

    moves.foldLeft[Option[Game]](Some(new Game)) { (game, move) =>
      val from = move("from").toInt
      val to = move("to").toInt
      val promoteTo = move.get("promoteTo") filter {_.length == 1} flatMap { s: String => chess.PieceType.fromChar(s.head) }
      game flatMap { _.makeMove(from, to, promoteTo) }
    }
      .flatMap { _.setCurrentPosition(positionIndex) }
      .flatMap { game: Game => Some(game.setPlayer(White, whitePlayer).setPlayer(Black, blackPlayer)) }
      .getOrElse(new Game)  
  }  
}
