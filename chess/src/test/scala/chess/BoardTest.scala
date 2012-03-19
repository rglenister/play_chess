package chess

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner 

import chess.PieceType._
import chess.PieceColor._


@RunWith(classOf[JUnitRunner]) 
class BoardSpec extends FlatSpec with ShouldMatchers {
 
  "The Board" should "convert algebraic notation to square index" in {
	Board.algebraicToSquareIndex("a1") should equal (0)
	Board.algebraicToSquareIndex("h8") should equal (63)
	Board.algebraicToSquareIndex("e8") should equal (60)
  }
}