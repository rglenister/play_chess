package chess


import PieceType._
import PieceColor._

/**
 * Factory for finding attacks to a square.
 */
object SquareAttackFinder {

  /**
   * Determines if a square is attacked.
   * 
   * @param position contains the piece locations.
   * @param toSquare is the square that may be being attacked.
   * @param byColor is color of the attacking pieces.
   * @return true if the given square is being attacked else false.
   */
  def isSquareAttacked(position: Position, toSquare: Int, byColor: PieceColor.Value): Boolean = {
    !findAttackingSquares(position, toSquare, byColor).isEmpty
  }
  
  /**
   * Finds the attacking squares.
   * 
   * @param position contains the piece locations.
   * @param toSquare is the square that may be being attacked.
   * @param byColor is color of the attacking pieces.
   * @return a list of attacking squares.
   */
  def findAttackingSquares(position: Position, toSquare: Int, byColor: PieceColor.Value): List[Int] = {
    new SquareAttackFinder(position, toSquare, byColor).findAttackingSquares()
  }
}

/**
 * Finds attacks to a square.
 * 
 * @param position contains the piece locations.
 * @param toSquare is the square that may be being attacked.
 * @param byColor is color of the attacking pieces.
 */
private class SquareAttackFinder(position: Position, toSquare: Int, byColor: PieceColor.Value) {
  
  /**
   * Finds the attacking squares.
   */
  def findAttackingSquares(): List[Int] = {
    List(Knight, Bishop, Rook) flatMap { findAttackingSquares(_) } flatten
  }
  
  /**
   * Finds the attacking squares.
   * 
   * @param toSquarePiece is the piece being used to find an attack.
   * @return a list of attacking squares.
   */
  def findAttackingSquares(toSquarePiece: PieceType.Value): List[Option[Int]] = {
  
    def emptySquare(square: Int): Option[Int] = { None }

    def occupiedSquare(square: Int, piece: Piece): Option[Int] = {
      if (piece.pieceColor == byColor && isPieceAttacking(toSquarePiece, piece.pieceType, square))
        Some(square)
      else None
    }
    new PieceSquareNotifier[Option[Int]](
        position, toSquare, Piece(toSquarePiece, PieceColor.otherColor(byColor))).generate(emptySquare, occupiedSquare)
  }
    
  /**
   * Determines if the given from square attacks the to square using the given from square piece.
   * 
   * @param toSquarePiece is the piece being used on the to square.
   * @param fromSquarePiece is the type of piece that is on the from square.
   * @param fromSquare if the from square. 
   */
  private def isPieceAttacking(toSquarePiece: PieceType.Value, fromSquarePiece: PieceType.Value, fromSquare: Int): Boolean = {
    attackPieceToFoundPieceMap(toSquarePiece).getOrElse(fromSquarePiece, (sq: Int) => false)(fromSquare)
  }
  
  private val attackPieceToFoundPieceMap: Map[PieceType.Value, Map[PieceType.Value, (Int) => Boolean]] = {
    Map(
      Knight -> Map(
        Knight -> { (fromSquare: Int) => true }
      ),
      Bishop -> Map(
        Pawn -> { fromSquare => Board.distance(fromSquare, toSquare) == 1 && Board.getPawnAttackSquares(byColor).contains(toSquare-fromSquare) }, 
        Bishop -> { fromSquare => true },
        King -> { fromSquare => Board.distance(fromSquare, toSquare) == 1 },
        Queen -> { fromSquare => true }
      ),  
      Rook -> Map(
        Rook -> { fromSquare => true },
        King -> { fromSquare => Board.distance(fromSquare, toSquare) == 1 },
        Queen -> { fromSquare => true }                  
      )
    )
  }
}
