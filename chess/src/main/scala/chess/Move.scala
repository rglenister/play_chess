package chess

/**
 * The base class of all moves.
 */
sealed abstract class Move {
  /**
   * @return the source square.
   */
  def fromSquare: Int
  
  /**
   * @return the destination square.
   */
  def toSquare: Int
  
  /**
   * @return true if this move is a capture else false.
   */
  def isCapture: Boolean
  
  /**
   * Gets the distance between the source and destination squares. The distance is the greater of the absolute difference
   * between the rows of the source and destination and the absolute difference between the columns.
   * 
   * @return the distance.
   */
  def distance: Int = Board.distance(toSquare, fromSquare)
}

/**
 * Standard implementation of move.
 * 
 * @param fromSquare is the source square.
 * @param toSquare is the destination square.
 * @param isCapture is true if this move is a capture else false.
 */
case class BasicMove(fromSquare: Int, toSquare: Int, isCapture: Boolean) extends Move

/**
 * En Passant capture move.
 * 
 * @param fromSquare is the source square.
 * @param toSquare is the destination square.
 */
case class EnPassantMove(fromSquare: Int, toSquare: Int) extends Move {
  def isCapture = true
}

/**
 * Pawn promotion move.
 * 
 * @param fromSquare is the source square.
 * @param toSquare is the destination square.
 * @param isCapture is true if this move is a capture else false.
 * @param promoteTo is the piece to promote the pawn to.
 */
case class PromotionMove(fromSquare: Int, toSquare: Int, isCapture: Boolean, promoteTo: PieceType.Value) extends Move

/**
 * Castling move.
 * 
 * @param fromSquare is the source square.
 * @param toSquare is the destination square.
 * @param boardSide is the side of the board.
 */
case class CastlingMove(fromSquare: Int, toSquare: Int, boardSide: BoardSide.Value) extends Move {
  def isCapture = false
}