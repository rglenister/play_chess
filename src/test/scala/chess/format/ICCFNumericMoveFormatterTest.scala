package chess.format


import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.EasyMockSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.easymock.EasyMock
import org.scalamock.annotation.mock

import chess.Board
import chess.Board.{algebraicToSquareIndex => aToI}
import chess.CastlingRights
import chess.Game
import chess.GamePosition
import MoveNotation._
import chess.Piece
import chess.PieceType._
import chess.PieceColor._
import chess.GamePosition

/**
 * Tests the ICCFNumericMoveFormatter.
 */
@RunWith(classOf[JUnitRunner]) 
class ICCFNumericMoveFormatterSpec extends FlatSpec with ShouldMatchers {
 
  val moveFormatter = MoveFormatter(ICCFNumeric)
  
  "The ICCFNumericMoveFormatter"  should "correctly format a standard opening move by white" in {
    val game = new Game().makeMove(aToI("e2"), aToI("e4")).get
    moveFormatter.formatMoves(game) should equal (List("5254"))
  }

  "The ICCFNumericMoveFormatter"  should "correctly format a standard opening move by black" in {
    val game1 = new Game().makeMove(aToI("e2"), aToI("e4")).get
    val game2 = game1.makeMove(aToI("b8"), aToI("c6")).get
    moveFormatter.formatMoves(game2) should equal (List("5254", "2836"))
  }

  it should "correctly format a promotion move by white" in {
    val squareToPieceMap = Map(55 -> Piece(Pawn, White), 10 -> Piece(King, Black), 12 -> Piece(King, White))
    val position = GamePosition(squareToPieceMap, White, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("h7"), aToI("h8"), Some(Queen)).get
    moveFormatter.formatMoves(game) should equal (List("87881"))
  }

  it should "correctly format a promotion move by black" in {
    val squareToPieceMap = Map(8 -> Piece(Pawn, Black), 10 -> Piece(King, Black), 12 -> Piece(King, White))
    val position = GamePosition(squareToPieceMap, Black, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("a2"), aToI("a1"), Some(Bishop)).get
    moveFormatter.formatMoves(game) should equal (List("12113"))
  }
}
