package chess.codec


import chess.Board
import chess.Position

import chess.PieceColor
import chess.BoardSide

import chess.PieceType._
import chess.PieceColor._
import chess.BoardSide._

/**
 * Encodes positions in Forsyth Edwards Notation.
 *
 * @param position is the position to encode.
 */
class FENEncoder(val position: Position) {
  
  /**
   * Encodes the position.
   * 
   * @return the FEN encoding.
   */
  def encode: String = {
    "%s %s %s %s %s %s".format(
        encodePiecePositions, encodeSideToMove, encodeCastlingRights, encodeEnPassant, encodeHalfmoveClock, encodeFullMoveNumber)
  }
  
  private def encodePiecePositions = {
    (56 to 0 by -8) map { n => List.range(n, n+8) } map { formatRow } map { runlengthEncodeSpaces } map { _.mkString } mkString("/")
  }
  
  private def formatRow(squareIndexes: List[Int]): List[Char] = {
    squareIndexes map { position.squareToPieceMap.get(_) } map {
      case Some(piece) => piece.toChar
      case None => ' '
    }
  }
  
  private def runlengthEncodeSpaces(row: List[Char]): List[Char] = {
    if (row.nonEmpty) {
      val (prefix, suffix) = row.span(_ == ' ')
      if (prefix.nonEmpty) prefix.length.toString.head :: runlengthEncodeSpaces(suffix)
      else suffix.head :: runlengthEncodeSpaces(suffix.tail)      
    } else Nil
  }

  private def encodeSideToMove = position.sideToMove.toString.head.toLower
  
  private def encodeCastlingRights: String = {
    val castlingRights = encodeCastlingRights(White) + encodeCastlingRights(Black).toLowerCase
    if (castlingRights.nonEmpty) castlingRights
    else "-"
  }
  
  private def encodeCastlingRights(pieceColor: PieceColor.Value): String = {
    BoardSide.values.toList filter { position.canCastle(pieceColor, _) } map { _.toString.head.toString } mkString
  }
  
  private def encodeEnPassant = position.enPassantSquare map {
    _ + (if (position.sideToMove == White) 8 else -8) } map {
    Board.squareIndexToAlgebraic(_)
  } getOrElse("-")
  
  private def encodeHalfmoveClock = position.fiftyMoveRuleCount
  
  private def encodeFullMoveNumber = {
    if (position.previousPositions.nonEmpty) position.previousPositions.length / 2 + 1
    else 1
  }
}