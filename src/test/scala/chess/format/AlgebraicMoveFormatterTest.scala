package chess.format


import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner
import org.junit.runner.RunWith
import org.easymock.EasyMock
//import org.scalamock.annotation.mock

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
 * Tests the AlgebraicMoveFormatter.
 */
@RunWith(classOf[JUnitRunner])
class AlgebraicMoveFormatterSpec extends AnyFlatSpec with Matchers {

  val moveFormatter = MoveFormatter(Algebraic)

  "The AlgebraicMoveFormatter" should "correctly format a standard opening move by white" in {
    val game = new Game().makeMove(aToI("e2"), aToI("e4")).get
    moveFormatter.formatMoves(game) should equal (List("e4"))
  }

  it should "correctly format a standard opening move by black" in {
    val game1 = new Game().makeMove(aToI("e2"), aToI("e4")).get
    val game2 = game1.makeMove(aToI("b8"), aToI("c6")).get
    moveFormatter.formatMoves(game2) should equal (List("e4", "\u265Ec6"))
  }

  it should "correctly format a promotion move by white" in {
    val squareToPieceMap = Map(55 -> Piece(Pawn, White), 10 -> Piece(King, Black), 12 -> Piece(King, White))
    val position = GamePosition(squareToPieceMap, White, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("h7"), aToI("h8"), Some(Queen)).get
    moveFormatter.formatMoves(game) should equal (List("h8\u2655"))
  }

  it should "correctly format a promotion move by black" in {
    val squareToPieceMap = Map(8 -> Piece(Pawn, Black), 10 -> Piece(King, Black), 12 -> Piece(King, White))
    val position = GamePosition(squareToPieceMap, Black, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("a2"), aToI("a1"), Some(Bishop)).get
    moveFormatter.formatMoves(game) should equal (List("a1\u265D"))
  }

  it should "correctly format a capture move" in {
    val squareToPieceMap = Map(aToI("a1") -> Piece(Rook, White), aToI("a8") -> Piece(Queen, Black), 10 -> Piece(King, Black), 12 -> Piece(King, White))
    val position = GamePosition(squareToPieceMap, Black, CastlingRights.create(squareToPieceMap), Some(aToI("h4")))
    val game = new Game(position).makeMove(aToI("a8"), aToI("a1")).get
    moveFormatter.formatMoves(game) should equal (List("\u265Bxa1"))
  }

  it should "correctly format an en passant capture move" in {
    val squareToPieceMap = Map(aToI("h4") -> Piece(Pawn, White), aToI("g4") -> Piece(Pawn, Black), 10 -> Piece(King, Black), 12 -> Piece(King, White))
    val position = GamePosition(squareToPieceMap, Black, CastlingRights.create(squareToPieceMap), Some(aToI("h4")))
    val game = new Game(position).makeMove(aToI("g4"), aToI("h3")).get
    moveFormatter.formatMoves(game) should equal (List("gxh3e.p"))
  }

  it should "correctly format a checking move" in {
    val squareToPieceMap = Map(aToI("a8") -> Piece(Rook, Black), 10 -> Piece(King, Black), aToI("e2") -> Piece(King, White))
    val position = GamePosition(squareToPieceMap, Black, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("a8"), aToI("e8")).get
    moveFormatter.formatMoves(game) should equal (List("\u265Ce8+"))
  }

  it should "correctly format a double checking move" in {
    val squareToPieceMap = Map(aToI("b7") -> Piece(Rook, White), aToI("c6") -> Piece(Bishop, White), 10 -> Piece(King, White), aToI("a8") -> Piece(King, Black))
    val position = GamePosition(squareToPieceMap, White, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("b7"), aToI("b8")).get
    moveFormatter.formatMoves(game) should equal (List("\u2656b8++"))
  }

  it should "correctly format a mating move" in {
    val squareToPieceMap = Map(aToI("h1") -> Piece(Rook, White), aToI("a6") -> Piece(King, White), aToI("a8") -> Piece(King, Black))
    val position = GamePosition(squareToPieceMap, White, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("h1"), aToI("h8")).get
    moveFormatter.formatMoves(game) should equal (List("\u2656h8#"))
  }

  it should "correctly format a move where two pieces of the same type and on the same row can both move to the destination square" in {
    val squareToPieceMap = Map(aToI("a1") -> Piece(Rook, White), aToI("c1") -> Piece(Rook, White), aToI("a6") -> Piece(King, White), aToI("a8") -> Piece(King, Black))
    val position = GamePosition(squareToPieceMap, White, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("a1"), aToI("b1")).get
    moveFormatter.formatMoves(game) should equal (List("\u2656ab1"))
  }

  it should "correctly format a move where two pieces of the same type and on the same column can both move to the destination square" in {
    val squareToPieceMap = Map(aToI("a1") -> Piece(Rook, White), aToI("a3") -> Piece(Rook, White), aToI("a6") -> Piece(King, White), aToI("a8") -> Piece(King, Black))
    val position = GamePosition(squareToPieceMap, White, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("a3"), aToI("a2")).get
    moveFormatter.formatMoves(game) should equal (List("\u26563a2"))
  }

  it should "correctly format a move where two pieces of different types and on the same column can both move to the destination square" in {
    val squareToPieceMap = Map(aToI("a1") -> Piece(Rook, White), aToI("a3") -> Piece(Queen, White), aToI("a6") -> Piece(King, White), aToI("a8") -> Piece(King, Black))
    val position = GamePosition(squareToPieceMap, White, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("a3"), aToI("a2")).get
    moveFormatter.formatMoves(game) should equal (List("\u2655a2"))
  }

  it should "correctly format a move where pieces of the same type are located on the same row and column as the moved piece can each move to the same destination square" in {
    val squareToPieceMap = Map(aToI("a1") -> Piece(Queen, White), aToI("a2") -> Piece(Queen, White), aToI("c1") -> Piece(Queen, White),
        aToI("c2") -> Piece(Queen, White), aToI("a6") -> Piece(King, White), aToI("a8") -> Piece(King, Black))
    val position = GamePosition(squareToPieceMap, White, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("c2"), aToI("b1")).get
    moveFormatter.formatMoves(game) should equal (List("\u2655c2b1"))
  }

}

/**
 * Tests the LongAlgebraicMoveFormatter.
 */
@RunWith(classOf[JUnitRunner])
class LongAlgebraicMoveFormatterSpec extends AnyFlatSpec with Matchers {

  val moveFormatter = MoveFormatter(LongAlgebraic)

  "The AlgebraicMoveFormatter" should "correctly format a standard opening move by white" in {
    val game = new Game().makeMove(aToI("e2"), aToI("e4")).get
    moveFormatter.formatMoves(game) should equal (List("e2-e4"))
  }

  it should "correctly format a standard opening move by black" in {
    val game1 = new Game().makeMove(aToI("e2"), aToI("e4")).get
    val game2 = game1.makeMove(aToI("b8"), aToI("c6")).get
    moveFormatter.formatMoves(game2) should equal (List("e2-e4", "\u265Eb8-c6"))
  }

  it should "correctly format a promotion move by white" in {
    val squareToPieceMap = Map(55 -> Piece(Pawn, White), 10 -> Piece(King, Black), 12 -> Piece(King, White))
    val position = GamePosition(squareToPieceMap, White, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("h7"), aToI("h8"), Some(Queen)).get
    moveFormatter.formatMoves(game) should equal (List("h7-h8\u2655"))
  }

  it should "correctly format a promotion move by black" in {
    val squareToPieceMap = Map(8 -> Piece(Pawn, Black), 10 -> Piece(King, Black), 12 -> Piece(King, White))
    val position = GamePosition(squareToPieceMap, Black, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("a2"), aToI("a1"), Some(Bishop)).get
    moveFormatter.formatMoves(game) should equal (List("a2-a1\u265D"))
  }

  it should "correctly format a capture move" in {
    val squareToPieceMap = Map(aToI("a1") -> Piece(Rook, White), aToI("a8") -> Piece(Queen, Black), 10 -> Piece(King, Black), 12 -> Piece(King, White))
    val position = GamePosition(squareToPieceMap, Black, CastlingRights.create(squareToPieceMap), Some(aToI("h4")))
    val game = new Game(position).makeMove(aToI("a8"), aToI("a1")).get
    moveFormatter.formatMoves(game) should equal (List("\u265Ba8xa1"))
  }

  it should "correctly format an en passant capture move" in {
    val squareToPieceMap = Map(aToI("h4") -> Piece(Pawn, White), aToI("g4") -> Piece(Pawn, Black), 10 -> Piece(King, Black), 12 -> Piece(King, White))
    val position = GamePosition(squareToPieceMap, Black, CastlingRights.create(squareToPieceMap), Some(aToI("h4")))
    val game = new Game(position).makeMove(aToI("g4"), aToI("h3")).get
    moveFormatter.formatMoves(game) should equal (List("g4xh3e.p"))
  }

  it should "correctly format a checking move" in {
    val squareToPieceMap = Map(aToI("a8") -> Piece(Rook, Black), 10 -> Piece(King, Black), aToI("e2") -> Piece(King, White))
    val position = GamePosition(squareToPieceMap, Black, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("a8"), aToI("e8")).get
    moveFormatter.formatMoves(game) should equal (List("\u265Ca8-e8+"))
  }

  it should "correctly format a double checking move" in {
    val squareToPieceMap = Map(aToI("b7") -> Piece(Rook, White), aToI("c6") -> Piece(Bishop, White), 10 -> Piece(King, White), aToI("a8") -> Piece(King, Black))
    val position = GamePosition(squareToPieceMap, White, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("b7"), aToI("b8")).get
    moveFormatter.formatMoves(game) should equal (List("\u2656b7-b8++"))
  }

  it should "correctly format a mating move" in {
    val squareToPieceMap = Map(aToI("h1") -> Piece(Rook, White), aToI("a6") -> Piece(King, White), aToI("a8") -> Piece(King, Black))
    val position = GamePosition(squareToPieceMap, White, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("h1"), aToI("h8")).get
    moveFormatter.formatMoves(game) should equal (List("\u2656h1-h8#"))
  }

  it should "correctly format a move where two pieces of the same type and on the same row can both move to the destination square" in {
    val squareToPieceMap = Map(aToI("a1") -> Piece(Rook, White), aToI("c1") -> Piece(Rook, White), aToI("a6") -> Piece(King, White), aToI("a8") -> Piece(King, Black))
    val position = GamePosition(squareToPieceMap, White, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("a1"), aToI("b1")).get
    moveFormatter.formatMoves(game) should equal (List("\u2656a1-b1"))
  }

  it should "correctly format a move where two pieces of the same type and on the same column can both move to the destination square" in {
    val squareToPieceMap = Map(aToI("a1") -> Piece(Rook, White), aToI("a3") -> Piece(Rook, White), aToI("a6") -> Piece(King, White), aToI("a8") -> Piece(King, Black))
    val position = GamePosition(squareToPieceMap, White, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("a3"), aToI("a2")).get
    moveFormatter.formatMoves(game) should equal (List("\u2656a3-a2"))
  }

  it should "correctly format a move where two pieces of different types and on the same column can both move to the destination square" in {
    val squareToPieceMap = Map(aToI("a1") -> Piece(Rook, White), aToI("a3") -> Piece(Queen, White), aToI("a6") -> Piece(King, White), aToI("a8") -> Piece(King, Black))
    val position = GamePosition(squareToPieceMap, White, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("a3"), aToI("a2")).get
    moveFormatter.formatMoves(game) should equal (List("\u2655a3-a2"))
  }

  it should "correctly format a move where pieces of the same type are located on the same row and column as the moved piece can each move to the same destination square" in {
    val squareToPieceMap = Map(aToI("a1") -> Piece(Queen, White), aToI("a2") -> Piece(Queen, White), aToI("c1") -> Piece(Queen, White),
        aToI("c2") -> Piece(Queen, White), aToI("a6") -> Piece(King, White), aToI("a8") -> Piece(King, Black))
    val position = GamePosition(squareToPieceMap, White, CastlingRights.create(squareToPieceMap))
    val game = new Game(position).makeMove(aToI("c2"), aToI("b1")).get
    moveFormatter.formatMoves(game) should equal (List("\u2655c2-b1"))
  }

}
