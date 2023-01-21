package chess.search


import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner
import org.junit.runner.RunWith

import chess.Board.{algebraicToSquareIndex => aToI}
import chess.BoardSide._
import chess.CastlingRights
import chess.GamePosition
import chess.Game
import chess.Move
import chess.BasicMove
import chess.PromotionMove
import chess.Piece
import chess.PieceType._
import chess.PieceColor._
import chess.Board
import chess.codec.FENParser



@RunWith(classOf[JUnitRunner])
class SearchSpec extends AnyFlatSpec with Matchers {

  "The Search" should "solve a mate in one" in {
	val gamePosition = FENParser.parse("2n5/8/6R1/1k1KQb2/2N1N3/8/R2B4/r1n2B2 w - - 0 1")
	val maxDepth = 1
	val searchResults = Search.search(gamePosition, maxDepth) should equal (SearchResults(9999, List(BasicMove(35, 27, false))))
  }

  "The Search" should "solve a mate in two (1)" in {
	val gamePosition = FENParser.parse("8/8/5KPk/8/B5P1/8/R3p3/8 w - - 0 1")
	val maxDepth = 3
	val searchResults = Search.search(gamePosition, maxDepth) should equal (
	  SearchResults(9997, List(BasicMove(24, 3, false), PromotionMove(12, 4, false, Knight), BasicMove(8, 15, false))))
  }
/*
  "The Search" should "solve a mate in two (3)" in {
	val gamePosition = FENParser.parse("2B5/Kb1N4/6pn/2Q5/5pk1/1q3N2/4pPp1/n5r1 w - - 0 1")
	val maxDepth = 3
	val searchResults = Search.search(gamePosition, maxDepth) should equal (
	  SearchResults(9997,List(BasicMove(34,39,false), BasicMove(46,39,true), BasicMove(51,36,false))))
  }

  "The Search" should "solve a mate in two (2)" in {
	val gamePosition = FENParser.parse("8/1pnbbprp/1Nkp1p1p/5P2/2rP1R2/N3B2P/4K3/8 w - - 0 1")
	val maxDepth = 3
	val searchResults = Search.search(gamePosition, maxDepth) should equal (
	  SearchResults(9997,List(BasicMove(27,35,false), BasicMove(50,35,true), BasicMove(29,26,true))))
  }

  "The Search" should "solve a mate in two (4)" in {
	val gamePosition = FENParser.parse("2N1K3/br2n3/q1pQ4/4pp2/1n2k3/4P1R1/Bp1R1p2/5r2 w - - 0 1")
	val maxDepth = 3
	val searchResults = Search.search(gamePosition, maxDepth) should equal (
	  SearchResults(9997,List(BasicMove(43,25,true), BasicMove(49,25,true), BasicMove(58,43,false))))
  }

  "The Search" should "solve a mate in two (5)" in {
	val gamePosition = FENParser.parse("8/KNkp4/2p5/8/2Q5/8/4B3/8 w - - 0 1")
	val maxDepth = 3
	val searchResults = Search.search(gamePosition, maxDepth) should equal (
	  SearchResults(9997,List(BasicMove(12,30,false), BasicMove(42,34,false), BasicMove(26,34,true))))
  }

  "The Search" should "solve a mate in three (1)" in {
	val gamePosition = FENParser.parse("4bB1k/6nr/5N2/4N3/8/8/8/K5R1 w - - 0 1")
	val maxDepth = 5
	val searchResults = Search.search(gamePosition, maxDepth) should equal (
	  SearchResults(9995,List(BasicMove(0,1,false), BasicMove(60,39,false), BasicMove(6,46,false), BasicMove(54,60,false),
	                BasicMove(46,62,false))))
  }

  "The Search" should "solve a mate in three (2)" in {
	val gamePosition = FENParser.parse("1bkr3r/pp1n1ppp/2q1pnb1/1N6/2BP1B2/5Q2/PPP3PP/4RR1K w - - 0 1")
	val maxDepth = 5
	val searchResults = Search.search(gamePosition, maxDepth) should equal (
	  SearchResults(9995,List(BasicMove(12,30,false), BasicMove(42,34,false), BasicMove(26,34,true))))
  }
 */
}