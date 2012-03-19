package chess


/**
 * Iterates through the available moves from a given square calling the user supplied functions for each.
 * 
 * @param position is the contains the positions of the pieces.
 * @param fromSquare is the source square.
 * @param piece is the piece for which the moves are to be generated.
 */
class PieceSquareNotifier[T](position: Position, fromSquare: Int, piece: Piece) {

  /**
   * Finds all the squares that the given piece can move to.
   * 
   * For each of the available squares one of the two given functions is called.
   * 
   * @param emptySquare is called for each empty square.
   * @param occupiedSquare is called for each occupied square.
   * @return a list of T
   */
  def generate(emptySquare: (Int) => T, occupiedSquare: (Int, Piece) => T): List[T] = {
    
    /**
     * Generates a list of T using given move lists.
     * 
     * @param movelists contains the lists of moves, one for each direction the piece can move in.
     * @return a list of T
     */
    def generate(moveLists: List[List[Int]]): List[T] = {
      moveLists flatMap { generateVector(_) }
    }
 
    /**
     * Generates a list of T for the given list of square indexes.
     */
    def generateVector(moves: List[Int]): List[T] = moves match {
      case h :: t if (position isEmpty h) => emptySquare(h) :: generateVector(t)
      case h :: t => List(occupiedSquare(h, position.squareToPieceMap(h)))
      case _ => Nil
    }  
    generate(StaticMoveGenerator.getMoves(fromSquare, piece))
  }  

}
