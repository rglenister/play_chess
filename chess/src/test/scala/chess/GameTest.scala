package chess

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.EasyMockSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner 
import org.easymock.EasyMock

import org.scalamock.annotation.mock

import chess.PieceType._
import chess.PieceColor._
import chess.BoardSide._

import Board.{algebraicToSquareIndex => aToI}


@RunWith(classOf[JUnitRunner]) 
class GameSpec extends FlatSpec with ShouldMatchers {
 
  "The Game" should "be ready for a new game" in {
    new Game().currentPosition should equal (GamePosition())
  }
  
  it should "add a move to its move list after making a valid move" in {
    new Game().makeMove(aToI("e2"), aToI("e4")).get.moveMap should have size (1)
  }
  
  it should "be able to go back to the previous position or forwards to the next position" in {
    val game1 = new Game
    val game2 = game1.makeMove(aToI("e2"), aToI("e4")).get
    game2.currentPosition should not equal (GamePosition())
    
    val game3 = game2.previousPosition.get
    game3.currentPosition should equal (game1.currentPosition)
    
    val game4 = game3.nextPosition.get
    game4.currentPosition should equal (game2.currentPosition)
  }
  
  it should "truncate the move list if a move is made from a previous position" in {
    val game1 = new Game
    val game2 = game1.makeMove(aToI("e2"), aToI("e4")).get
    val game3 = game2.previousPosition.get   
    game3.moveMap should have size (1)
    
    val game4 = game3.makeMove(aToI("d2"), aToI("d4")).get
    game4.moveMap should have size (1)
    game4.currentPosition should not equal (game2.currentPosition)
  }
  
  it should "select the promotion move with the correct promotion piece" in {
    val squareToPieceMap = Map(aToI("e1") -> Piece(King, White), aToI("e8") -> Piece(King, Black), aToI("b7") -> Piece(Pawn, White))
    val position = GamePosition(squareToPieceMap, White, CastlingRights.create(squareToPieceMap))
    val game1 = new Game(position)
    val game2 = game1.makeMove(aToI("b7"), aToI("b8"), Some(Bishop)).get
    game2.currentPosition.squareToPieceMap(aToI("b8")) should equal (Piece(Bishop, White))
  }
  
  it should "return None if an illegal move is attempted" in {
    new Game().makeMove(aToI("e2"), aToI("d3")) should equal (None)   
  }
  
  it should "provide details of each player" in {
    new Game().players(White) should equal (Player("White Player"))
    new Game().players(Black) should equal (Player("Black Player"))
  }

  it should "allow the details of each player to be updated" in {
    new Game().setPlayer(White, Player("New White Player")).players(White) should equal (Player("New White Player"))
    new Game().setPlayer(Black, Player("New Black Player")).players(Black) should equal (Player("New Black Player"))
  }
}
