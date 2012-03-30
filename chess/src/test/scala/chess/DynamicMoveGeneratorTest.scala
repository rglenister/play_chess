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
 

/**
 * Tests the DynamicMoveGenerator
 */
@RunWith(classOf[JUnitRunner]) 
class DynamicMoveGeneratorSpec extends FlatSpec with EasyMockSugar with ShouldMatchers {

  "The DynamicMoveGenerator" should "generate two moves for a white pawn on rank two" in {
    val squareToPieceMap = Map(9 -> Piece(Pawn, White))
    val mockPosition = createMockPosition(squareToPieceMap, White)    
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition)
    result should equal (List(BasicMove(9, 17, false), BasicMove(9, 25, false)))
  }
  
  "The DynamicMoveGenerator" should "generate two moves for a black pawn on rank two" in {
    val squareToPieceMap = Map(48 -> Piece(Pawn, Black))
    val mockPosition = createMockPosition(squareToPieceMap, Black)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition)
    result should equal (List(BasicMove(48, 40, false), BasicMove(48, 32, false)))
  }
  
  it should "generate one move for a pawn on rank three" in {
    val squareToPieceMap = Map(17 -> Piece(Pawn, White))
    val mockPosition = createMockPosition(squareToPieceMap, White)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition)
    result should equal (List(BasicMove(17, 25, false)))
  }

  it should "generate no moves for a pawn blocked by another pawn of the same color" in {
    val whiteSquareToPieceMap = Map(17 -> Piece(Pawn, White), 25 -> Piece(Pawn, White))
    val mockPosition = createMockPosition(whiteSquareToPieceMap, White)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition)
    result should equal (List(BasicMove(25, 33, false)))
  }

  it should "generate two capturing moves for a pawn that has opposing pieces ahead and to either side" in {
    val squareToPieceMap = Map(17 -> Piece(Pawn, White), 24 -> Piece(Pawn, Black), 26 -> Piece(Pawn, Black))
    val mockPosition = createMockPosition(squareToPieceMap, White)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition)
    result should equal (List(BasicMove(17, 25, false), BasicMove(17, 24, true), BasicMove(17, 26, true)))
  }

  it should "generate one capturing move for a pawn on the edge of the board that has opposing pieces ahead and to either side" in {
    val squareToPieceMap = Map(16 -> Piece(Pawn, White), 23 -> Piece(Pawn, Black), 25 -> Piece(Pawn, Black))
    val mockPosition = createMockPosition(squareToPieceMap, White)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition)
    result should equal (List(BasicMove(16, 24, false), BasicMove(16, 25, true)))
  }

  it should "generate no capturing moves for a pawn attacking pieces of the same color" in {
    val squareToPieceMap = Map(17 -> Piece(Pawn, White), 24 -> Piece(Pawn, White), 26 -> Piece(Pawn, White))
    val mockPosition = createMockPosition(squareToPieceMap, White)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition)
    result should equal (List(BasicMove(17, 25, false), BasicMove(24, 32, false), BasicMove(26, 34, false)))
  }

  it should "generate four moves for a white pawn advancing to the eighth rank when no captures are available" in {
    val squareToPieceMap = Map(48 -> Piece(Pawn, White))
    val mockPosition = createMockPosition(squareToPieceMap, White)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition)
    result should equal (List(PromotionMove(48, 56, false, Knight), PromotionMove(48, 56, false, Bishop), PromotionMove(48, 56, false, Rook), PromotionMove(48, 56, false, Queen)))
  }

  it should "generate four moves for a black pawn advancing to the eighth rank when no captures are available" in {
    val squareToPieceMap = Map(8 -> Piece(Pawn, Black))
    val mockPosition = createMockPosition(squareToPieceMap, Black)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition)
    result should equal (List(PromotionMove(8, 0, false, Knight), PromotionMove(8, 0, false, Bishop), PromotionMove(8, 0, false, Rook), PromotionMove(8, 0, false, Queen)))
  }

  it should "generate twelve moves for a white pawn advancing to the eighth rank when two captures are available" in {
    val squareToPieceMap = Map(50 -> Piece(Pawn, White), 57 -> Piece(Bishop, Black), 59 -> Piece(Bishop, Black))
    val mockPosition = createMockPosition(squareToPieceMap, White)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition)
    result should equal (List(
        PromotionMove(50, 58, false, Knight), PromotionMove(50, 58, false, Bishop), PromotionMove(50, 58, false, Rook), PromotionMove(50, 58, false, Queen),
        PromotionMove(50, 57, true, Knight), PromotionMove(50, 57, true, Bishop), PromotionMove(50, 57, true, Rook), PromotionMove(50, 57, true, Queen),
        PromotionMove(50, 59, true, Knight), PromotionMove(50, 59, true, Bishop), PromotionMove(50, 59, true, Rook), PromotionMove(50, 59, true, Queen)))
  }

  it should "generate twelve moves for a black pawn advancing to the eighth rank when two captures are available" in {
    val squareToPieceMap = Map(9 -> Piece(Pawn, Black), 0 -> Piece(Bishop, White), 2 -> Piece(Bishop, White))
    val mockPosition = createMockPosition(squareToPieceMap, Black)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition)
    result should equal (List(
        PromotionMove(9, 1, false, Knight), PromotionMove(9, 1, false, Bishop), PromotionMove(9, 1, false, Rook), PromotionMove(9, 1, false, Queen),
        PromotionMove(9, 2, true, Knight), PromotionMove(9, 2, true, Bishop), PromotionMove(9, 2, true, Rook), PromotionMove(9, 2, true, Queen),
        PromotionMove(9, 0, true, Knight), PromotionMove(9, 0, true, Bishop), PromotionMove(9, 0, true, Rook), PromotionMove(9, 0, true, Queen)))
  }

  it should "generate an en passant capture for a white pawn alongside a black pawn that has just moved two squares forward" in {
    val squareToPieceMap = Map(33 -> Piece(Pawn, White), 34 -> Piece(Pawn, Black))
    val mockPosition = createMockPosition(squareToPieceMap, White, Some(34))
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition)
    result should equal (List(BasicMove(33, 41, false), EnPassantMove(33, 42, 34)))
  }

  it should "generate an en passant capture for a black pawn alongside a white pawn that has just moved two squares forward" in {
    val squareToPieceMap = Map(24 -> Piece(Pawn, White), 25 -> Piece(Pawn, Black))
    val mockPosition = createMockPosition(squareToPieceMap, Black, Some(24))
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition)
    result should equal (List(BasicMove(25, 17, false), EnPassantMove(25, 16, 24)))
  }

  it should "generate a capture for a knight attacking an opposing piece but not for a piece of the same color" in {
    val squareToPieceMap = Map(0 -> Piece(Knight, White), 10 -> Piece(Pawn, Black), 17 -> Piece(Pawn, White))
    val mockPosition = createMockPosition(squareToPieceMap, White)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition)
    result should equal (List(BasicMove(0, 10, true), BasicMove(17,25, false)))
  }

  it should "generate a capture for a bishop attacking an opposing piece but not for a piece of the same color" in {
    val squareToPieceMap = Map(9 -> Piece(Bishop, White), 0 -> Piece(Bishop, Black), 18 -> Piece(Pawn, White))
    val mockPosition = createMockPosition(squareToPieceMap, White)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition)
    result should equal (List(BasicMove(9,16, false), BasicMove(9, 0, true), BasicMove(9, 2, false), BasicMove(18, 26, false)))
  }

  it should "generate a capture for a rook attacking an opposing piece but not for a piece of the same color" in {
    val squareToPieceMap = Map(0 -> Piece(Rook, White), 8 -> Piece(Bishop, Black), 1 -> Piece(Knight, White))
    val mockPosition = createMockPosition(squareToPieceMap, White)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition)
    result should equal (List(BasicMove(0, 8, true), BasicMove(1, 11, false), BasicMove(1, 18, false), BasicMove(1, 16, false)))
  }

  it should "not generate castling moves for white if white does not have castling rights" in {
    val squareToPieceMap = Map(4 -> Piece(King, White))
    val mockPosition = createMockPosition(squareToPieceMap, White)
    EasyMock.expect(mockPosition.canCastle(White, Kingside)).andReturn(false)
    EasyMock.expect(mockPosition.canCastle(White, Queenside)).andReturn(false)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition) filter { move => move.isInstanceOf[CastlingMove] }
    result should equal (List())
  }

  it should "not generate castling moves for black if black does not have castling rights" in {
    val squareToPieceMap = Map(60 -> Piece(King, Black))
    val mockPosition = createMockPosition(squareToPieceMap, Black)
    EasyMock.expect(mockPosition.canCastle(Black, Kingside)).andReturn(false)
    EasyMock.expect(mockPosition.canCastle(Black, Queenside)).andReturn(false)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition) filter { move => move.isInstanceOf[CastlingMove] }
    result should equal (List())
  }

  it should "generate castling moves for white if white has castling rights" in {
    val squareToPieceMap = Map(4 -> Piece(King, White))
    val mockPosition = createMockPosition(squareToPieceMap, White)
    EasyMock.expect(mockPosition.canCastle(White, Kingside)).andReturn(true)
    EasyMock.expect(mockPosition.canCastle(White, Queenside)).andReturn(true)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition) filter { move => move.isInstanceOf[CastlingMove] }
    result should equal (List(CastlingMove(4,6,Kingside), CastlingMove(4,2,Queenside)))
  }

  it should "generate castling moves for black if black has castling rights" in {
    val squareToPieceMap = Map(60 -> Piece(King, Black))
    val mockPosition = createMockPosition(squareToPieceMap, Black)
    EasyMock.expect(mockPosition.canCastle(Black, Kingside)).andReturn(true)
    EasyMock.expect(mockPosition.canCastle(Black, Queenside)).andReturn(true)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition) filter { move => move.isInstanceOf[CastlingMove] }
    result should equal (List(CastlingMove(60,62,Kingside), CastlingMove(60,58,Queenside)))
  }

  it should "not generate castling moves for white if the king would pass through check" in {
    val squareToPieceMap = Map(4 -> Piece(King, White), 12 -> Piece(Bishop, Black))
    val mockPosition = createMockPosition(squareToPieceMap, White)
    EasyMock.expect(mockPosition.canCastle(White, Kingside)).andReturn(true)
    EasyMock.expect(mockPosition.canCastle(White, Queenside)).andReturn(true)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition) filter { move => move.isInstanceOf[CastlingMove] }
    result should equal (List())
  }

  it should "not generate castling moves for black if the king would pass through check" in {
    val squareToPieceMap = Map(60 -> Piece(King, Black), 52 -> Piece(Bishop, White))
    val mockPosition = createMockPosition(squareToPieceMap, Black)
    EasyMock.expect(mockPosition.canCastle(Black, Kingside)).andReturn(true)
    EasyMock.expect(mockPosition.canCastle(Black, Queenside)).andReturn(true)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition) filter { move => move.isInstanceOf[CastlingMove] }
    result should equal (List())
  }
  
  it should "not generate castling moves for white if the king is in check" in {
    val squareToPieceMap = Map(4 -> Piece(King, White), 13 -> Piece(Bishop, Black))
    val mockPosition = createMockPosition(squareToPieceMap, White)
    EasyMock.expect(mockPosition.canCastle(White, Kingside)).andReturn(true)
    EasyMock.expect(mockPosition.canCastle(White, Queenside)).andReturn(true)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition) filter { move => move.isInstanceOf[CastlingMove] }
    result should equal (List())
  }

  it should "not generate castling moves for black if the king is in check" in {
    val squareToPieceMap = Map(60 -> Piece(King, Black), 51 -> Piece(Bishop, White))
    val mockPosition = createMockPosition(squareToPieceMap, Black)
    EasyMock.expect(mockPosition.canCastle(Black, Kingside)).andReturn(true)
    EasyMock.expect(mockPosition.canCastle(Black, Queenside)).andReturn(true)
    EasyMock.replay(mockPosition)
    
    val result = DynamicMoveGenerator.getMoveList(mockPosition) filter { move => move.isInstanceOf[CastlingMove] }
    result should equal (List())
  }
  
  it should "not generate a kingside castling move for white unless all squares between king and rook are empty" in {
    for (square <- 5 to 6) {
      val squareToPieceMap = Map(4 -> Piece(King, White), square -> Piece(Knight, White))
	  val mockPosition = createMockPosition(squareToPieceMap, White)
	  EasyMock.expect(mockPosition.canCastle(White, Kingside)).andReturn(true)
	  EasyMock.expect(mockPosition.canCastle(White, Queenside)).andReturn(false)
	  EasyMock.replay(mockPosition)
	    
	  val result = DynamicMoveGenerator.getMoveList(mockPosition) filter { move => move.isInstanceOf[CastlingMove] }
	  result should equal (List())  
    }
  }

  it should "not generate a queenside castling move for white unless all squares between king and rook are empty" in {
    for (square <- 1 to 3) {
      val squareToPieceMap = Map(4 -> Piece(King, White), square -> Piece(Knight, White))
	  val mockPosition = createMockPosition(squareToPieceMap, White)
	  EasyMock.expect(mockPosition.canCastle(White, Kingside)).andReturn(false)
	  EasyMock.expect(mockPosition.canCastle(White, Queenside)).andReturn(true)
	  EasyMock.replay(mockPosition)
	    
	  val result = DynamicMoveGenerator.getMoveList(mockPosition) filter { move => move.isInstanceOf[CastlingMove] }
	  result should equal (List())  
    }
  }

  it should "not generate a kingside castling move for black unless all squares between king and rook are empty" in {
    for (square <- 61 to 62) {
      val squareToPieceMap = Map(60 -> Piece(King, Black), square -> Piece(Knight, White))
	  val mockPosition = createMockPosition(squareToPieceMap, White)
	  EasyMock.expect(mockPosition.canCastle(White, Kingside)).andReturn(true)
	  EasyMock.expect(mockPosition.canCastle(White, Queenside)).andReturn(false)
	  EasyMock.replay(mockPosition)
	    
	  val result = DynamicMoveGenerator.getMoveList(mockPosition) filter { move => move.isInstanceOf[CastlingMove] }
	  result should equal (List())  
    }
  }

  it should "not generate a queenside castling move for black unless all squares between king and rook are empty" in {
    for (square <- 57 to 59) {
      val squareToPieceMap = Map(60 -> Piece(King, Black), square -> Piece(Knight, White))
	  val mockPosition = createMockPosition(squareToPieceMap, White)
	  EasyMock.expect(mockPosition.canCastle(White, Kingside)).andReturn(false)
	  EasyMock.expect(mockPosition.canCastle(White, Queenside)).andReturn(true)
	  EasyMock.replay(mockPosition)
	    
	  val result = DynamicMoveGenerator.getMoveList(mockPosition) filter { move => move.isInstanceOf[CastlingMove] }
	  result should equal (List())  
    }
  }
 
  val emptySquareToPieceMap = Map[Int, Piece]()
  
  def createMockPosition(squareToPieceMap: Map[Int, Piece], sideToMove: PieceColor.Value, enpassantSquare: Option[Int] = None) = {
    val mockPosition = mock[Position]
    EasyMock.expect(mockPosition.sideToMove).andReturn(sideToMove).anyTimes
    EasyMock.expect(mockPosition.opposingSide).andReturn(PieceColor.otherColor(sideToMove)).anyTimes
    EasyMock.expect(mockPosition squareToPieceMap).andReturn(squareToPieceMap).anyTimes
    EasyMock.expect(mockPosition squareToPieceMap White).andReturn(squareToPieceMap filter (_._2.pieceColor == White)).anyTimes
    EasyMock.expect(mockPosition squareToPieceMap Black).andReturn(squareToPieceMap filter (_._2.pieceColor == Black)).anyTimes
    EasyMock.expect(mockPosition enPassantSquare).andReturn(enpassantSquare).anyTimes
    for (square <- 0 until Board.NumSquares)
      EasyMock.expect(mockPosition.isEmpty(square)).andReturn(!squareToPieceMap.contains(square)).anyTimes
    mockPosition    
  }
}