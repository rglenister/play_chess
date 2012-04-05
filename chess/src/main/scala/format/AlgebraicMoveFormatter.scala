package chess.format

import chess.BoardSide.Kingside
import chess.PieceType.Pawn
import chess.Board
import chess.CastlingMove
import chess.EnPassantMove
import chess.GameMove
import chess.GameStatus
import chess.Move
import chess.Piece
import chess.PieceType
import chess.PromotionMove


/**
 * Formats moves in the standard algebraic format.
 * 
 * @see http://en.wikipedia.org/wiki/Algebraic_chess_notation
 */
class AlgebraicMoveFormatter extends MoveFormatter {
  override protected[this] def formatMove(gameMove: GameMove) = {
    gameMove.move match {
      case CastlingMove(_, _, boardSide) => (if (boardSide == Kingside) "O-O" else "O-O-O") + getResult(gameMove)
      case move: Move => basicFormat(gameMove)
    }
  }
    
  private def basicFormat(gameMove: GameMove) = {      
    "%s%s%s%s%s%s%s".format(
      getPiece(gameMove),
      getFromSquare(gameMove),
      getFromToSeparator(gameMove),
      getToSquare(gameMove), 
      getPromotionPiece(gameMove),
      getEnPassantIndicator(gameMove),
      getResult(gameMove)
    )
  }
  
  private def getPiece(gameMove: GameMove) = getMovedPiece(gameMove) match {
    case Piece(Pawn, _) => ""
    case piece => MoveFormatter.pieceCharToUnicode(piece.toChar).toString
  }
  
  private def getFromSquare(gameMove: GameMove) = getMovedPiece(gameMove) match {
    case Piece(Pawn, _) => getFromSquareForPawn(gameMove)
    case _ => getFromSquareForPiece(gameMove)
  }
  
  private def getFile(square: Int) = {
    Board.squareIndexToAlgebraic(square).head.toString
  }
  
  private def getFromSquareForPawn(gameMove: GameMove) = gameMove.move.isCapture match {
    case true => getFile(gameMove.move.fromSquare)
    case _ => ""
  }
  
  private def getFromSquareForPiece(gameMove: GameMove) = {
    val move = gameMove.move
    val otherMovesToSameSquare = gameMove.position.moveList.filter {
      m => m.toSquare == gameMove.move.toSquare &&
        getMovedPiece(gameMove.position, m) == getMovedPiece(gameMove) &&
        m != move
    }
    if (otherMovesToSameSquare.isEmpty) {
      ""
    } else {
      val algebraic = Board.squareIndexToAlgebraic(gameMove.move.fromSquare)
      if (otherMovesToSameSquare.filter { m => Board.column(m.fromSquare) == Board.column(move.fromSquare) } isEmpty) {
        algebraic.head.toString
      } else if (otherMovesToSameSquare.filter { m => Board.row(m.fromSquare) == Board.row(move.fromSquare) } isEmpty) {
        algebraic.last.toString
      } else {
        algebraic
      }
    }
  }
  
  private def getFromToSeparator(gameMove: GameMove) = if (gameMove.move.isCapture) "x" else ""

  private def getToSquare(gameMove: GameMove) = Board.squareIndexToAlgebraic(gameMove.move.toSquare)
  
  private def getPromotionPiece(gameMove: GameMove) = gameMove.move match {
    case PromotionMove(_, _, _, promoteTo) => MoveFormatter.pieceCharToUnicode(Piece(promoteTo, gameMove.position.sideToMove).toChar) 
    case _ => ""
  }
  
  private def getEnPassantIndicator(gameMove: GameMove) = gameMove.move match {
    case EnPassantMove(_, _, _) => "e.p"
    case _ => ""    
  }
    
  private def getResult(gameMove: GameMove) = gameMove.nextPosition.gameStatus match {
    case GameStatus.Checkmate => "#"
    case _ => "+" * gameMove.nextPosition.checkCount
  }
}
