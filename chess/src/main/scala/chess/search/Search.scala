package chess.search

import chess.Position
import chess.Move
import chess.BasicMove
import chess.GamePosition
import chess.GameStatus._
import chess.PieceColor._


/**
 * Search results.
 * 
 * @param score is the score given.
 * @param bestLine contains the move sequence that resulted in the score.
 */
case class SearchResults(score: Int, bestLine: List[Move]) extends Ordered[SearchResults] {
  def compare(other: SearchResults) = {
    score.compare(other.score)
  }
  
  def unary_- = SearchResults(-score, bestLine)
}

/**
 * Brute force full width search. 
 */
object Search {

  /** The maximum possible score */
  val MaxScore = 10000
  
  /**
   * Scores the given position using a full width search.
   * 
   * @param position is the position to score.
   * @param maxDepth is the maximum number of half moves to look ahead.
   * @return the search results.
   */
  def search(position: Position, maxDepth: Int): SearchResults = {
    -doSearch(position, List(), 0, maxDepth)
  }
  
  private def doSearch(position: Position, moves: List[Move], depth: Int, maxDepth: Int): SearchResults = {
    val score = scorePosition(position, depth)
    if (score != 0 || depth == maxDepth || position.gameStatus != InProgress) {
      SearchResults(score, moves.reverse)
    } else {
      -(position.moveList.map { move =>
	    doSearch(GamePosition(position, move).get, move :: moves, depth + 1, maxDepth)
      } max)
    }
  }
  
  private def scorePosition(position: Position, depth: Int): Int = {
    if (position.gameStatus == Checkmate) {
      MaxScore - depth
    } else {
      0
    } * (if (position.sideToMove == White) 1 else -1)
  }
}
