package chess.codec


import scala.Array.canBuildFrom

import chess.PieceColor.Black
import chess.PieceColor.White
import chess.Board
import chess.BoardSide
import chess.CastlingRights
import chess.GamePosition
import chess.Piece
import chess.PieceColor
import chess.Position


/**
 * Factory for FENEncoders.
 */
object FENSerializer {
  private val rowsOfSquareIndexes = (56 to 0 by -8) map { n => List.range(n, n+8) }
  
  def apply(position: Position) = new FENSerializer(position)
}

/**
 * Encodes positions in Forsyth Edwards Notation.
 *
 * @param position is the position to encode.
 */
class FENSerializer(val position: Position) {
  
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
    FENSerializer.rowsOfSquareIndexes map { formatRow } map { runlengthEncodeSpaces } map { (_.mkString) } mkString("/")
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

/**
 * FEN parser.
 */
object FENParser {
  
  /**
   * Creates a position from the given FEN.
   * 
   * @param fen contains the position in Forsyth Edwards Notation.
   * @return the position representing the given fen.
   */
  def parse(fen: String): GamePosition = {
    val FenRegEx = """([pnbrqkPNBRQK12345678/]+) ([wb]) (K?Q?k?q?-?) ([a-e][1-8]|-) (\d+) (\d+)""".r
    val FenRegEx(fenBoard, fenSideToMove, fenCastlingAvailability, fenEnPassantSquare, fenHalfmoveClock, fenFullMoveNumber) = fen
    val sideToMove = PieceColor(fenSideToMove.head).get
    GamePosition(
      parseBoard(fenBoard),
      sideToMove,
      CastlingRights.create(fenCastlingAvailability),
      if (fenEnPassantSquare == "-") None else Some(Board.algebraicToSquareIndex(fenEnPassantSquare) + (if (sideToMove==White) -8 else 8)),
      fenHalfmoveClock.toInt,
      fenFullMoveNumber.toInt
    )
  }  

  /**
   * Converts the first component of a FEN to a square to piece map.
   * 
   * @param fenBoard is the / separated FEN string representation of the board.
   * @return a square to piece map.
   */
  private def parseBoard(fenBoard: String): Map[Int, Piece] = {
    val encodedFenBoard = fenBoard.split("/").reverse.foldLeft("")(_ + _)
    val decodedFenBoard = encodedFenBoard.foldLeft("") { (result, ch) => 
      result + (if (ch.isDigit) " " * ch.toString.toInt else ch)
    }
    decodedFenBoard.toArray.zipWithIndex.foldLeft(Map[Int, Piece]()) {
      case (squareToPieceMap, pieceAndIndex) => pieceAndIndex match {
        case (piece, index) =>
          if (piece != ' ') squareToPieceMap + (index -> Piece(piece)) else squareToPieceMap
      }
    }
  }  
}
