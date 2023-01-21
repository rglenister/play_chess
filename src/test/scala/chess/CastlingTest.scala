package chess


import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner
import org.junit.runner.RunWith

import chess.PieceType._
import chess.PieceColor._
import chess.BoardSide._


/**
 * Tests the CastlingMetadata
 */
@RunWith(classOf[JUnitRunner])
class CastlingMetadataSpec extends AnyFlatSpec with Matchers {

  "The CastlingMetadata" should "should be correct for white on the kingside" in {
    val castlingMetadata = CastlingMetadata.get(White)(Kingside)
    castlingMetadata.kingFromSquare should equal (4)
    castlingMetadata.kingToSquare should equal (6)
    castlingMetadata.rookFromSquare should equal (7)
    castlingMetadata.rookToSquare should equal (5)
    castlingMetadata.squaresBetweenKingAndRook should equal (List(5, 6))
  }

  it should "be correct for white on the queenside" in {
    val castlingMetadata = CastlingMetadata.get(White)(Queenside)
    castlingMetadata.kingFromSquare should equal (4)
    castlingMetadata.kingToSquare should equal (2)
    castlingMetadata.rookFromSquare should equal (0)
    castlingMetadata.rookToSquare should equal (3)
    castlingMetadata.squaresBetweenKingAndRook should equal (List(3, 2, 1))
  }

  it should "be correct for black on the kingside" in {
    val castlingMetadata = CastlingMetadata.get(Black)(Kingside)
    castlingMetadata.kingFromSquare should equal (60)
    castlingMetadata.kingToSquare should equal (62)
    castlingMetadata.rookFromSquare should equal (63)
    castlingMetadata.rookToSquare should equal (61)
    castlingMetadata.squaresBetweenKingAndRook should equal (List(61, 62))
  }

  it should "be correct for black on the queenside" in {
    val castlingMetadata = CastlingMetadata.get(Black)(Queenside)
    castlingMetadata.kingFromSquare should equal (60)
    castlingMetadata.kingToSquare should equal (58)
    castlingMetadata.rookFromSquare should equal (56)
    castlingMetadata.rookToSquare should equal (59)
    castlingMetadata.squaresBetweenKingAndRook should equal (List(59, 58, 57))
  }
}


/**
 * Tests the CastlingRights
 */
@RunWith(classOf[JUnitRunner])
class CastlingRightsSpec extends AnyFlatSpec with Matchers {

  "The CastlingRights" should "be all false for an empty position" in {
    val squareToPieceMap: Map[Int, Piece] = Map()
    val castlingRights = CastlingRights.next(squareToPieceMap)
    castlingRights(White).canCastle(Kingside) should equal (false)
    castlingRights(White).canCastle(Queenside) should equal (false)
    castlingRights(Black).canCastle(Kingside) should equal (false)
    castlingRights(Black).canCastle(Queenside) should equal (false)
  }

  it should "not allow castling rights to be regained if the kings and rooks move back to their home squares" in {
    val castlingRights1 = CastlingRights.next(Map())
    castlingRights1(White).canCastle(Kingside) should equal (false)
    castlingRights1(White).canCastle(Queenside) should equal (false)
    castlingRights1(Black).canCastle(Kingside) should equal (false)
    castlingRights1(Black).canCastle(Queenside) should equal (false)

    val squareToPieceMap: Map[Int, Piece] = Map(4 -> Piece(King, White), 0 -> Piece(Rook, White), 7 -> Piece(Rook, White),
                                                60 -> Piece(King, Black), 56 -> Piece(Rook, Black), 63 -> Piece(Rook, Black))
    val castlingRights2 = CastlingRights.next(squareToPieceMap, castlingRights1)
    castlingRights2(White).canCastle(Kingside) should equal (false)
    castlingRights2(White).canCastle(Queenside) should equal (false)
    castlingRights2(Black).canCastle(Kingside) should equal (false)
    castlingRights2(Black).canCastle(Queenside) should equal (false)
  }

  it should "be all true when all kings and rooks are on their home squares" in {
    val squareToPieceMap: Map[Int, Piece] = Map(4 -> Piece(King, White), 0 -> Piece(Rook, White), 7 -> Piece(Rook, White),
                                                60 -> Piece(King, Black), 56 -> Piece(Rook, Black), 63 -> Piece(Rook, Black))
    val castlingRights = CastlingRights.next(squareToPieceMap)
    castlingRights(White).canCastle(Kingside) should equal (true)
    castlingRights(White).canCastle(Queenside) should equal (true)
    castlingRights(Black).canCastle(Kingside) should equal (true)
    castlingRights(Black).canCastle(Queenside) should equal (true)
  }

  it should "not allow white to castle kingside if the kingside rook is removed" in {
    val squareToPieceMap: Map[Int, Piece] = Map(4 -> Piece(King, White), 0 -> Piece(Rook, White),
                                                60 -> Piece(King, Black), 56 -> Piece(Rook, Black), 63 -> Piece(Rook, Black))
    val castlingRights = CastlingRights.next(squareToPieceMap)
    castlingRights(White).canCastle(Kingside) should equal (false)
    castlingRights(White).canCastle(Queenside) should equal (true)
    castlingRights(Black).canCastle(Kingside) should equal (true)
    castlingRights(Black).canCastle(Queenside) should equal (true)
  }

  it should "not allow black to castle kingside if the kingside rook is replaced by another piece" in {
    val squareToPieceMap: Map[Int, Piece] = Map(4 -> Piece(King, White), 0 -> Piece(Rook, White), 7 -> Piece(Rook, White),
                                                60 -> Piece(King, Black), 56 -> Piece(Rook, Black), 63 -> Piece(Knight, Black))
    val castlingRights = CastlingRights.next(squareToPieceMap)
    castlingRights(White).canCastle(Kingside) should equal (true)
    castlingRights(White).canCastle(Queenside) should equal (true)
    castlingRights(Black).canCastle(Kingside) should equal (false)
    castlingRights(Black).canCastle(Queenside) should equal (true)
  }

  it should "not allow white to castle queenside if the queenside rook is removed" in {
    val squareToPieceMap: Map[Int, Piece] = Map(4 -> Piece(King, White), 7 -> Piece(Rook, White),
                                                60 -> Piece(King, Black), 56 -> Piece(Rook, Black), 63 -> Piece(Rook, Black))
    val castlingRights = CastlingRights.next(squareToPieceMap)
    castlingRights(White).canCastle(Kingside) should equal (true)
    castlingRights(White).canCastle(Queenside) should equal (false)
    castlingRights(Black).canCastle(Kingside) should equal (true)
    castlingRights(Black).canCastle(Queenside) should equal (true)
  }

  it should "not allow black to castle queenside if the queenside rook is removed" in {
    val squareToPieceMap: Map[Int, Piece] = Map(4 -> Piece(King, White), 0 -> Piece(Rook, White), 7 -> Piece(Rook, White),
                                                60 -> Piece(King, Black), 63 -> Piece(Rook, Black))
    val castlingRights = CastlingRights.next(squareToPieceMap)
    castlingRights(White).canCastle(Kingside) should equal (true)
    castlingRights(White).canCastle(Queenside) should equal (true)
    castlingRights(Black).canCastle(Kingside) should equal (true)
    castlingRights(Black).canCastle(Queenside) should equal (false)
  }

  it should "not allow white to castle on either side if the king is removed" in {
    val squareToPieceMap: Map[Int, Piece] = Map(7 -> Piece(Rook, White), 0 -> Piece(Rook, White),
                                                60 -> Piece(King, Black), 56 -> Piece(Rook, Black), 63 -> Piece(Rook, Black))
    val castlingRights = CastlingRights.next(squareToPieceMap)
    castlingRights(White).canCastle(Kingside) should equal (false)
    castlingRights(White).canCastle(Queenside) should equal (false)
    castlingRights(Black).canCastle(Kingside) should equal (true)
    castlingRights(Black).canCastle(Queenside) should equal (true)
  }

  it should "not allow black to castle on either side if the king is replaced with another piece" in {
    val squareToPieceMap: Map[Int, Piece] = Map(4 -> Piece(King, White), 0 -> Piece(Rook, White), 7 -> Piece(Rook, White),
                                                60 -> Piece(Bishop, White), 56 -> Piece(Rook, Black), 63 -> Piece(Rook, Black))
    val castlingRights = CastlingRights.next(squareToPieceMap)
    castlingRights(White).canCastle(Kingside) should equal (true)
    castlingRights(White).canCastle(Queenside) should equal (true)
    castlingRights(Black).canCastle(Kingside) should equal (false)
    castlingRights(Black).canCastle(Queenside) should equal (false)
  }

  it should "be all false when initialized from an empty FEN" in {
    val castlingRights = CastlingRights.create("-")
    castlingRights(White).canCastle(Kingside) should equal (false)
    castlingRights(White).canCastle(Queenside) should equal (false)
    castlingRights(Black).canCastle(Kingside) should equal (false)
    castlingRights(Black).canCastle(Queenside) should equal (false)
  }

  it should "be all true when initialized from a FEN that includes full castling rights for both sides" in {
    val castlingRights = CastlingRights.create("KQkq")
    castlingRights(White).canCastle(Kingside) should equal (true)
    castlingRights(White).canCastle(Queenside) should equal (true)
    castlingRights(Black).canCastle(Kingside) should equal (true)
    castlingRights(Black).canCastle(Queenside) should equal (true)
  }
}
