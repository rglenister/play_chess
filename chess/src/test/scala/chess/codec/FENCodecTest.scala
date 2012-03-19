package chess.codec


import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner 

import chess.Board.{algebraicToSquareIndex => aToI}
import chess.CastlingRights
import chess.GamePosition
import chess.Game
import chess.Piece

import chess.PieceType._
import chess.PieceColor._


@RunWith(classOf[JUnitRunner]) 
class FENEncoderSpec extends FlatSpec with ShouldMatchers {
 
  "The FEN Encoder" should "correctly encode the start position" in {
	val fenEncoder = new FENEncoder(GamePosition())
	fenEncoder.encode should equal ("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
  }
  
  it should "correctly encode the position after move 1. e4" in {
    val game1 = new Game().makeMove(aToI("e2"), aToI("e4")).get
	val fenEncoder = new FENEncoder(game1.currentPosition)
	fenEncoder.encode should equal ("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1")
  }

  it should "correctly encode the position after move 1. ... c5:" in {
    val game1 = new Game().makeMove(aToI("e2"), aToI("e4")).get
    val game2 = game1.makeMove(aToI("c7"), aToI("c5")).get
	val fenEncoder = new FENEncoder(game2.currentPosition)
	fenEncoder.encode should equal ("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2")
  }

  it should "correctly encode the position after move 2. Nf3:" in {
    val game1 = new Game().makeMove(aToI("e2"), aToI("e4")).get
    val game2 = game1.makeMove(aToI("c7"), aToI("c5")).get
    val game3 = game2.makeMove(aToI("g1"), aToI("f3")).get
	val fenEncoder = new FENEncoder(game3.currentPosition)
	fenEncoder.encode should equal ("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2")
  }
  
  it should "correctly encode the empty position" in {
  	val position = GamePosition(Map(), White, CastlingRights.create(Map()))
	val fenEncoder = new FENEncoder(position)
	fenEncoder.encode should equal ("8/8/8/8/8/8/8/8 w - - 0 1")
  }
  
  it should "correctly encode the castling rights when white can only castle kingside and black can only castle queenside" in {
    val squareToPieceMap: Map[Int, Piece] = Map(4 -> Piece(King, White), 7 -> Piece(Rook, White),
                                                60 -> Piece(King, Black), 56 -> Piece(Rook, Black))
  	val position = GamePosition(squareToPieceMap, White, CastlingRights.create(squareToPieceMap))
	new FENEncoder(position).encode should equal ("r3k3/8/8/8/8/8/8/4K2R w Kq - 0 1")
  }
  
  
  
}