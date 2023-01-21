package chess

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner
import org.junit.runner.RunWith

import org.easymock.EasyMock
import org.scalamock.scalatest.MockFactory

import chess.PieceType._
import chess.PieceColor._

/**
 * Tests the PieceSquareNotifier.
 */
@RunWith(classOf[JUnitRunner])
class PieceSquareNotifierSpec extends AnyFunSuite with Matchers with MockFactory {

  test("Iteration") {
    val squareToPieceMap = Map(0 -> Piece(King, White), 1 -> Piece(Knight, Black))

    val mockPosition = mock[Position]
    val mockListener = mock[Listener[String]]

    EasyMock.expect(mockPosition isEmpty 9).andReturn(true)
    EasyMock.expect(mockListener emptySquare 9) andReturn "e9"
    EasyMock.expect(mockPosition isEmpty 1).andReturn(false)
    EasyMock.expect(mockPosition.squareToPieceMap).andReturn(squareToPieceMap)
    EasyMock.expect(mockListener.occupiedSquare(1, Piece(Knight, Black))) andReturn "o1"
    EasyMock.expect(mockPosition isEmpty 8).andReturn(true)
    EasyMock.expect(mockListener emptySquare 8) andReturn "e8"

    EasyMock.replay(mockListener)
    EasyMock.replay(mockPosition)

    val pieceSquareNotifier = new PieceSquareNotifier[String](mockPosition, 0, Piece(King, White))
    val result = pieceSquareNotifier.generate(mockListener.emptySquare, mockListener.occupiedSquare)
    result should equal(List("e9", "o1", "e8"))
  }

  trait Listener[T] {
    def emptySquare(square: Int): T
    def occupiedSquare(square: Int, piece: Piece): T
  }
}
