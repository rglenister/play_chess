package chess.format


import chess.Board
import chess.Move
import chess.GameMove
import chess.PromotionMove

import chess.PieceType._

/**
 * Formats moves in the International Correspondence Chess Federation numeric format.
 * 
 * @see http://en.wikipedia.org/wiki/ICCF_numeric_notation
 */
class ICCFNumericMoveFormatter extends MoveFormatter {

  override def formatMove(gameMove: GameMove)  = {
    val move = gameMove.move
    "%s%s%s".format(formatSquare(move.fromSquare), formatSquare(move.toSquare), getPromotionPiece(move))
  }
  
  private def formatSquare(square: Int) = {
    "%s%s".format(Board.column(square) + 1, Board.row(square) + 1)
  }
    
  private def getPromotionPiece(move: Move) = {
    move match {
      case PromotionMove(_, _, _, Queen) => "1"
      case PromotionMove(_, _, _, Rook) => "2"
      case PromotionMove(_, _, _, Bishop) => "3" 
      case PromotionMove(_, _, _, Knight) => "4" 
      case _ => ""
    }
  }
}
