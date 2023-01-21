package chess.format


import chess.PieceType
import chess.PieceType._
import chess.GameMove
import chess.Game
import chess.Move
import chess.Piece
import chess.Position

/**
 * Enumeration of the supported move formats.
 */
object MoveNotation extends Enumeration {

  val Algebraic, LongAlgebraic, ICCFNumeric = Value 
}

import MoveNotation._


object MoveFormatter {

  def apply(moveNotation: MoveNotation.Value = Algebraic) = moveNotation match {
    case Algebraic => new AlgebraicMoveFormatter
    case LongAlgebraic => new LongAlgebraicMoveFormatter
    case ICCFNumeric => new ICCFNumericMoveFormatter
  }
  
  val pieceCharToUnicode = Map(
    'P' -> '\u2659',
    'N' -> '\u2658',
    'B' -> '\u2657',
    'R' -> '\u2656',
    'Q' -> '\u2655',
    'K' -> '\u2654',
    'p' -> '\u265F',
    'n' -> '\u265E',
    'b' -> '\u265D',
    'r' -> '\u265C',
    'q' -> '\u265B',
    'k' -> '\u265A'  
  )
}

/**
 * 
 */
trait MoveFormatter {

  /**
   * Formats the moves played in the given game.
   * 
   * @param game is the game containing the moves.
   * @return a list of string representations of the given moves.
   */
  def formatMoves(game: Game) = {
    game.moveMap.map {
      case (_, gameMove) => formatMove(gameMove)
    }
  }
  
  /**
   * Formats a move.
   * 
   * @param gameMove is the move to format.
   * @return the formatted move.
   */
  protected[this] def formatMove(gameMove: GameMove): String
  
  /**
   * Gets the piece making the given move.
   * 
   * @param gameMove is the move.
   * @return the moved piece.
   */
  def getMovedPiece(gameMove: GameMove): Piece = getMovedPiece(gameMove.position, gameMove.move)
  
  /**
   * Gets the piece making the given move.
   * 
   * @param position contains the game position when the move was made.
   * @param move is the move.
   * @return the moved piece.
   */
  def getMovedPiece(position: Position, move: Move): Piece = position.squareToPieceMap(move.fromSquare)
}  
