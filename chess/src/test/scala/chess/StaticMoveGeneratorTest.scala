package chess

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner 

import chess.PieceType._
import chess.PieceColor._

import StaticMoveGenerator.getMoves

/**
 * Tests the StaticMoveGenerator
 */
@RunWith(classOf[JUnitRunner]) 
class StaticMoveGeneratorSpec extends FlatSpec with ShouldMatchers {

  "The MoveGenerator" should "generate no moves for a pawn on rank one" in {
    getMoves(0, Piece(Pawn, White)) should equal (Nil)
    getMoves(63, Piece(Pawn, Black)) should equal (Nil)
  }

  it should "generate no moves for a pawn on rank eight" in {
    getMoves(0, Piece(Pawn, Black)) should equal (Nil)
    getMoves(63, Piece(Pawn, White)) should equal (Nil)
  }

  it should "generate two moves for a pawn that hasn't moved" in {
    getMoves(8, Piece(Pawn, White)) should equal (List(List(16, 24)))
    getMoves(55, Piece(Pawn, Black)) should equal (List(List(47, 39)))
  }

  it should "generate one move for a pawn that has moved" in {
    getMoves(16, Piece(Pawn, White)) should equal (List(List(24)))
    getMoves(47, Piece(Pawn, Black)) should equal (List(List(39)))
  }
  
  it should "generate two moves for a knight on a corner square" in {
    getMoves(0, Piece(Knight, White)) should equal (List(List(10), List(17)))    
  }
  
  it should "generate eight moves for a knight out in the open" in {
    getMoves(36, Piece(Knight, White)) should equal (List(List(46), List(53), List(51), List(42), List(26), List(19), List(21), List(30)))    
  }
  
  it should "generate seven moves for a bishop on a corner square" in {
    getMoves(0, Piece(Bishop, White)) should equal (List(List(9, 18, 27, 36, 45, 54, 63)))    
  }
  
  it should "generate moves in four directions for a bishop not on an edge of the board" in {
    getMoves(9, Piece(Bishop, White)) should equal (List(List(18, 27, 36, 45, 54, 63), List(16), List(0), List(2)))    
  }
  
  it should "generate moves in two directions for a rook on a corner square" in {
    getMoves(0, Piece(Rook, White)) should equal (List(List(1, 2, 3, 4, 5, 6, 7), List(8, 16, 24, 32, 40, 48, 56)))    
  }
  
  it should "generate moves in four directions for a rook not on the edge of the board" in {
    getMoves(9, Piece(Rook, White)) should equal (List(List(10, 11, 12, 13, 14, 15), List(17, 25, 33, 41, 49, 57), List(8), List(1)))    
  }
  
  it should "generate moves in three directions for a queen on a corner square" in {
    getMoves(0, Piece(Queen, White)) should equal (List(List(9, 18, 27, 36, 45, 54, 63), List(1, 2, 3, 4, 5, 6, 7), List(8, 16, 24, 32, 40, 48, 56)))    
  }
  
  it should "generate moves in eight directions for a queen not on the edge of the board" in {
    getMoves(9, Piece(Queen, White)) should equal (List(List(18, 27, 36, 45, 54, 63), List(16), List(0), List(2), List(10, 11, 12, 13, 14, 15), List(17, 25, 33, 41, 49, 57), List(8), List(1)))    
  }
  
  it should "generate moves in three directions for a king on a corner square" in {
    getMoves(63, Piece(King, White)) should equal (List(List(54), List(62), List(55)))    
  }
  
  it should "generate moves in eight directions for a king not on the edge of the board" in {
    getMoves(54, Piece(King, White)) should equal (List(List(63), List(61), List(45), List(47), List(55), List(62), List(53), List(46)))    
  }
}