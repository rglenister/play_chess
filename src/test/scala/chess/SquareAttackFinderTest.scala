package chess


import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner
import org.junit.runner.RunWith

import org.easymock.EasyMock

import org.scalamock.scalatest.MockFactory

import chess.PieceType._
import chess.PieceColor._
import chess.BoardSide._



/**
 * Tests the DynamicMoveGenerator
 */
@RunWith(classOf[JUnitRunner])
class SquareAttackFinderSpec extends AnyFlatSpec with Matchers with MockFactory {

  "The SquareAttackFinder" should "find a single attacking square" in {
    val squareToPieceMap = Map(63 -> Piece(Bishop, Black))
    val mockPosition = createMockPosition(squareToPieceMap)
    EasyMock.replay(mockPosition)

    val result = SquareAttackFinder.findAttackingSquares(mockPosition, 0, Black)
    result should equal (List(63))
  }

  it should "ignore attacking pieces of the incorrect color" in {
    val squareToPieceMap = Map(63 -> Piece(Bishop, White))
    val mockPosition = createMockPosition(squareToPieceMap)
    EasyMock.replay(mockPosition)

    val result = SquareAttackFinder.findAttackingSquares(mockPosition, 0, Black)
    result should equal (List())
  }

  it should "find a multiple attacking squares" in {
    val squareToPieceMap = Map(56 -> Piece(Rook, Black), 7 -> Piece(Rook, Black), 63 -> Piece(Bishop, Black))
    val mockPosition = createMockPosition(squareToPieceMap)
    EasyMock.replay(mockPosition)

    val result = SquareAttackFinder.findAttackingSquares(mockPosition, 0, Black)
    result should equal (List(63, 7, 56))
  }

  it should "only allow a knight to attack from 2-1 squares away" in {
    val squareToPieceMap = Map(38 -> Piece(Knight, Black), 45 -> Piece(Bishop, Black), 43 -> Piece(Rook, Black))
    val mockPosition = createMockPosition(squareToPieceMap)
    EasyMock.replay(mockPosition)

    val result = SquareAttackFinder.findAttackingSquares(mockPosition, 28, Black)
    result should equal (List(38))
  }

  it should "only allow bishops and queens to attack along a diagonal from a distance greater than one square" in {
    val squareToPieceMap = Map(46 -> Piece(Bishop, Black), 14 -> Piece(Queen, Black), 10 -> Piece(Rook, Black), 42 -> Piece(King, Black))
    val mockPosition = createMockPosition(squareToPieceMap)
    EasyMock.replay(mockPosition)

    val result = SquareAttackFinder.findAttackingSquares(mockPosition, 28, Black)
    result should equal (List(46, 14))
  }

  it should "allow a king to attack an adjacent square" in {
    val kingSquares = List(0, 1, 2, 8, 10, 16, 17, 18)
    val squareToPieceMap = Map(kingSquares map { (_, Piece(King, Black)) }: _*)
    val mockPosition = createMockPosition(squareToPieceMap)
    EasyMock.replay(mockPosition)

    val result = SquareAttackFinder.findAttackingSquares(mockPosition, 9, Black).sorted
    result should equal (kingSquares)
  }

  it should "not allow a white pawn to attack the square ahead of it" in {
    val squareToPieceMap = Map(41 -> Piece(Pawn, White))
    val mockPosition = createMockPosition(squareToPieceMap)
    EasyMock.replay(mockPosition)

    val result = SquareAttackFinder.findAttackingSquares(mockPosition, 49, White)
    result should equal (List())
  }

  it should "not allow a black pawn to attack the square ahead of it" in {
    val squareToPieceMap = Map(17 -> Piece(Pawn, Black))
    val mockPosition = createMockPosition(squareToPieceMap)
    EasyMock.replay(mockPosition)

    val result = SquareAttackFinder.findAttackingSquares(mockPosition, 9, Black)
    result should equal (List())
  }

  it should "allow a white pawn to attack the two squares immediately diagonally ahead of it" in {
    val squareToPieceMap = Map(40 -> Piece(Pawn, White), 42 -> Piece(Pawn, White))
    val mockPosition = createMockPosition(squareToPieceMap)
    EasyMock.replay(mockPosition)

    val result = SquareAttackFinder.findAttackingSquares(mockPosition, 49, White)
    result should equal (List(40, 42))
  }

  it should "allow a black pawn to attack the two squares immediately diagonally ahead of it" in {
    val squareToPieceMap = Map(16 -> Piece(Pawn, Black), 18 -> Piece(Pawn, Black))
    val mockPosition = createMockPosition(squareToPieceMap)
    EasyMock.replay(mockPosition)

    val result = SquareAttackFinder.findAttackingSquares(mockPosition, 9, Black)
    result should equal (List(18, 16))
  }

  def createMockPosition(squareToPieceMap: Map[Int, Piece]): Position = {
    val mockPosition = mock[Position]
    EasyMock.expect(mockPosition squareToPieceMap).andReturn(squareToPieceMap).anyTimes
    for (square <- 0 until Board.NumSquares)
      EasyMock.expect(mockPosition.isEmpty(square)).andReturn(!squareToPieceMap.contains(square)).anyTimes
    mockPosition
  }
}