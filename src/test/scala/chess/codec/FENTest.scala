package chess.codec


import chess.Board.{algebraicToSquareIndex => aToI}
import chess.BoardSide._
import chess.PieceColor._
import chess.PieceType._
import chess._
import org.junit.runner.RunWith
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner


//@RunWith(classOf[JUnitRunner])
class FENSerializerSpec extends AnyFlatSpec with Matchers {

  "The FEN Encoder" should "correctly encode the start position" in {
	val fenEncoder = FENSerializer(GamePosition())
	fenEncoder.encode should equal ("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
  }

  it should "correctly encode the position after move 1. e4" in {
    val game1 = new Game().makeMove(aToI("e2"), aToI("e4")).get
	val fenEncoder = FENSerializer(game1.currentPosition)
	fenEncoder.encode should equal ("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1")
  }

  it should "correctly encode the position after move 1. ... c5:" in {
    val game1 = new Game().makeMove(aToI("e2"), aToI("e4")).get
    val game2 = game1.makeMove(aToI("c7"), aToI("c5")).get
	val fenEncoder = FENSerializer(game2.currentPosition)
	fenEncoder.encode should equal ("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2")
  }

  it should "correctly encode the position after move 2. Nf3:" in {
    val game1 = new Game().makeMove(aToI("e2"), aToI("e4")).get
    val game2 = game1.makeMove(aToI("c7"), aToI("c5")).get
    val game3 = game2.makeMove(aToI("g1"), aToI("f3")).get
	val fenEncoder = FENSerializer(game3.currentPosition)
	fenEncoder.encode should equal ("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2")
  }

  it should "correctly encode the empty position" in {
  	val position = GamePosition(Map(), White, CastlingRights.create(Map[Int, Piece]()))
	val fenEncoder = FENSerializer(position)
	fenEncoder.encode should equal ("8/8/8/8/8/8/8/8 w - - 0 1")
  }

  it should "correctly encode the castling rights when white can only castle kingside and black can only castle queenside" in {
    val squareToPieceMap: Map[Int, Piece] = Map(4 -> Piece(King, White), 7 -> Piece(Rook, White),
                                                60 -> Piece(King, Black), 56 -> Piece(Rook, Black))
  	val position = GamePosition(squareToPieceMap, White, CastlingRights.create(squareToPieceMap))
	FENSerializer(position).encode should equal ("r3k3/8/8/8/8/8/8/4K2R w Kq - 0 1")
  }



}


@RunWith(classOf[JUnitRunner])
class FENParserSpec extends AnyFlatSpec with Matchers {
  "The FEN Parser" should "initialize correctly from a FEN" in {
    val gamePosition = FENParser.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
    gamePosition.squareToPieceMap should equal (Board.startingPosition)
    gamePosition.sideToMove should equal (White)
    gamePosition.castlingRightsMap(White) should equal (CastlingRights(kingHasMoved = false, Map(Kingside -> false, Queenside -> false)))
    gamePosition.castlingRightsMap(Black) should equal (CastlingRights(kingHasMoved = false, Map(Kingside -> false, Queenside -> false)))
    gamePosition.fiftyMoveRuleCount should equal (0)
    gamePosition.fullMoveNumber should equal (1)
    gamePosition.repetitionOfPositionCount should equal (0)
    gamePosition.enPassantSquare should equal (None)
  }

  it should "initialize correctly the side to move from a FEN" in {
    val gamePosition = FENParser.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1")
    gamePosition.sideToMove should equal (Black)
  }

  it should "initialize correctly from a FEN with empty castling rights" in {
    val gamePosition = FENParser.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b - - 0 1")
    gamePosition.castlingRightsMap(White) should equal (CastlingRights(kingHasMoved = false, Map(Kingside -> true, Queenside -> true)))
    gamePosition.castlingRightsMap(Black) should equal (CastlingRights(kingHasMoved = false, Map(Kingside -> true, Queenside -> true)))
  }

  it should "initialize correctly from a FEN with mixed castling rights" in {
    val gamePosition = FENParser.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w Kq - 0 1")
    gamePosition.castlingRightsMap(White) should equal (CastlingRights(kingHasMoved = false, Map(Kingside -> false, Queenside -> true)))
    gamePosition.castlingRightsMap(Black) should equal (CastlingRights(kingHasMoved = false, Map(Kingside -> true, Queenside -> false)))
  }

  it should "initialize correctly from a FEN with the enpassant square set" in {
    val gamePosition = FENParser.parse("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1")
    gamePosition.enPassantSquare should equal (Some(28))
  }

  it should "initialize move counters correctly from a FEN" in {
    val gamePosition = FENParser.parse("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 2 10")
    gamePosition.fiftyMoveRuleCount should equal (2)
    gamePosition.fullMoveNumber should equal (10)
  }
}