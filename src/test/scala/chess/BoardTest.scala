package chess

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner
import org.junit.runner.RunWith

import chess.PieceType._
import chess.PieceColor._


@RunWith(classOf[JUnitRunner]) 
class BoardSpec extends AnyFlatSpec with Matchers {
 
  "The Board" should "convert algebraic notation to square index" in {
	Board.algebraicToSquareIndex("a1") should equal (0)
	Board.algebraicToSquareIndex("h8") should equal (63)
	Board.algebraicToSquareIndex("e8") should equal (60)
  }
}