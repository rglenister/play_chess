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
class PositionSpec extends FlatSpec with EasyMockSugar with ShouldMatchers {
 
  "The Position" should "know the color of the side to move and the opposing side" in {
    val whiteToMove = new TestPosition(Map(), White)
    whiteToMove.sideToMove should equal (White)
    whiteToMove.opposingSide should equal (Black)
    
    val blackToMove = new TestPosition(Map(), Black)
    blackToMove.sideToMove should equal (Black)
    blackToMove.opposingSide should equal (White)
  }

  it should "know if a square is empty" in {
    new TestPosition(Map(), White) isEmpty 0 should equal (true)
    new TestPosition(Map(0 -> Piece(Pawn, White)), White) isEmpty 0 should equal (false)
  }

  it should "know what piece is on a square" in {
    new TestPosition(Map(0 -> Piece(Pawn, White)), White) squareToPieceMap 0 should equal (Piece(Pawn, White))
  }

  it should "know what square a piece is on" in {
    val squareToPieceMap = Map(8 -> Piece(Pawn, White), 9 -> Piece(Pawn, White), 10 -> Piece(Bishop, Black))
    val pieceToSquaresMap = new TestPosition(squareToPieceMap, White).pieceToSquaresMap
    pieceToSquaresMap(Piece(Pawn, White)) should equal (List(8, 9))
    pieceToSquaresMap(Piece(Bishop, Black)) should equal (List(10))
  }

  it should "know the location of the kings" in {
    val position = new TestPosition(Map(7 -> Piece(King, White), 8 -> Piece(King, Black)), White)
    position.getKingSquare(White) should equal (7)
    position.getKingSquare(Black) should equal (8)
  }

  it should "know the castling rights map" in {
    val whiteCastlingRights = mock[CastlingRights]
    val blackCastlingRights = mock[CastlingRights]
    val castlingRightsMap = Map(White -> whiteCastlingRights, Black -> blackCastlingRights)
    val position = new TestPosition(Map(), White, castlingRightsMap)
    position.castlingRightsMap should be theSameInstanceAs (castlingRightsMap)
  }

  it should "give the correct castling rights" in {
    val castlingRightsMap = Map(White -> mock[CastlingRights], Black -> mock[CastlingRights])
    for (color <- PieceColor.values; boardSide <- BoardSide.values) {
      EasyMock.expect(castlingRightsMap(color).canCastle(boardSide).andReturn(false))
      EasyMock.expect(castlingRightsMap(color).canCastle(boardSide).andReturn(true))
    }
    val position = new TestPosition(Map(), White, castlingRightsMap)
    EasyMock.replay(castlingRightsMap(White))
    EasyMock.replay(castlingRightsMap(Black))

    for (color <- PieceColor.values; boardSide <- BoardSide.values) {
      position.canCastle(color, boardSide) should equal (false)
      position.canCastle(color, boardSide) should equal (true)
    }
  }
  
  it should "know the en passant square" in {
    val position1 = new TestPosition(Map(), White, Map(), Some(3))
    position1.enPassantSquare should be (Some(3))

    val position2 = new TestPosition(Map(), White, Map(), None)
    position2.enPassantSquare should be (None)
  }

  class TestPosition(
    val squareToPieceMap: Map[Int, Piece],
    val sideToMove: PieceColor.Value,
    val castlingRightsMap: Map[PieceColor.Value, CastlingRights] = Map(),
    val enPassantSquare: Option[Int] = None,
    val fiftyMoveRuleCount: Int = 0,
    val fullMoveNumber: Int = 1,
    val previousPositions: List[Position] = Nil) extends Position {
  }
}


@RunWith(classOf[JUnitRunner]) 
class GamePositionSpec extends FlatSpec with ShouldMatchers {
 
  "The GamePosition" should "be ready for a new game" in {
    val gamePosition = GamePosition()
    gamePosition.squareToPieceMap should equal (Board.startingPosition)
    gamePosition.sideToMove should equal (White)
    gamePosition.castlingRightsMap(White) should equal (CastlingRights(false, Map(Kingside -> false, Queenside -> false)))
    gamePosition.castlingRightsMap(Black) should equal (CastlingRights(false, Map(Kingside -> false, Queenside -> false)))
    gamePosition.fiftyMoveRuleCount should equal (0)
    gamePosition.fullMoveNumber should equal (1)
    gamePosition.repetitionOfPositionCount should equal (0)
    gamePosition.enPassantSquare should equal (None)
    gamePosition.isCheck should equal (false)
    gamePosition.previousPositions should equal (Nil)
    gamePosition.moveList should have length (20)
  }

  it should "checkmate when the side to move cannot get out of check" in {
    val gamePosition1 = GamePosition()
    val gamePosition2 = GamePosition(gamePosition1, getMove(gamePosition1, "f2 f3")).get
    val gamePosition3 = GamePosition(gamePosition2, getMove(gamePosition2, "e7 e5")).get
    val gamePosition4 = GamePosition(gamePosition3, getMove(gamePosition3, "g2 g4")).get
    val gamePosition5 = GamePosition(gamePosition4, getMove(gamePosition4, "d8 h4")).get
    gamePosition5.isCheck should equal (true)
    gamePosition5.moveList.length should be (0)
    gamePosition5.gameStatus should equal (GameStatus.Checkmate)
  }

  it should "stalemate when the side to move cannot move but is not in check" in {
    val squareToPieceMap = Map(aToI("g6") -> Piece(Queen, White), aToI("f7") -> Piece(King, White), aToI("h8") -> Piece(King, Black))
    val gamePosition = GamePosition(squareToPieceMap, Black, CastlingRights.create(squareToPieceMap))
    gamePosition.isCheck should equal (false)
    gamePosition.moveList.length should be (0)
    gamePosition.gameStatus should equal (GameStatus.Stalemate)
  }

  private def getMove(gamePosition: GamePosition, move: String): Move = {
    move.split("\\W") match {
      case Array(from, to) => gamePosition.moveList.find(m => m.fromSquare==aToI(from) && m.toSquare==aToI(to)) get 
    }
  }
}