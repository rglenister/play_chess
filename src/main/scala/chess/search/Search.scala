package chess.search

import chess.Position
import chess.Move
import chess.BasicMove
import chess.Game
import chess.GamePosition
import chess.GameStatus._
import chess.PieceColor._
import chess.format.MoveFormatter
import chess.format.MoveNotation._
import java.io.PrintStream
import org.apache.log4j._


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
  
  val logger = Logger.getLogger(getClass)
  logger.addAppender(new ConsoleAppender(new PatternLayout()))
  
  var nodeCountTimer = new NodeCountTimer

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
    nodeCountTimer = new NodeCountTimer
    val searchResults = -doSearch(position, List(), 0, maxDepth)
    System.out.println("score: %d, formattedMoves: %s, nodeCountTimer: %s".format(searchResults.score, MoveFormatter(LongAlgebraic).formatMoves(Game(position, searchResults.bestLine)), nodeCountTimer))
    System.out.flush
    searchResults
  }
  
  private def doSearch(position: Position, moves: List[Move], depth: Int, maxDepth: Int): SearchResults = {
    nodeCountTimer.increment
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

class Timer {
  val startTime = System.currentTimeMillis
  lazy val ellapsedTimeMillis = System.currentTimeMillis - startTime
}

class NodeCountTimer {
  val timer = new Timer
  var nodeCount = 0L
 
  def increment { nodeCount += 1 }
  
  override def toString = "%d nodes in %1.3f seconds (%d nps)".format(nodeCount, timer.ellapsedTimeMillis / 1000.0, nodeCount * 1000 / timer.ellapsedTimeMillis)
}
