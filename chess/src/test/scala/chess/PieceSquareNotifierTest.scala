package chess


import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.EasyMockSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner 
import org.easymock.EasyMock

import chess.PieceType._
import chess.PieceColor._

import org.scalamock.annotation.mock
 
/**
 * Tests the PieceSquareNotifier.
 */
@RunWith(classOf[JUnitRunner]) 
class PieceSquareNotifierSpec extends FunSuite with EasyMockSugar with ShouldMatchers {

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
