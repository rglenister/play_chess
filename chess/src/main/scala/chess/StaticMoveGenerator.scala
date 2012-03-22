package chess


/**
 * Factory that creates a lookup table containing the moves for a piece on a given square.
 *
 */
object StaticMoveGenerator {
  import PieceType._
  import PieceColor._

  /**
   * Gets a list of lists of moves for the given piece on the given square.
   * 
   * @return a list of lists of moves e.g. for a rook on a corner square the returned list would contain two lists each with length
   *         seven. One of these lists would contain all the squares on the same row and the other all the squares on the same file.
   */
  def getMoves(fromSquare: Int, piece: Piece): List[List[Int]] = moveLists(piece.pieceColor.id)(piece.pieceType.id)(fromSquare)

  /**
   * Determines whether adding an increment to a square index takes it off the edge of the board.
   * 
   * @param fromSquare is the originating square.
   * @param toSquare is the destination square.
   * @return true if the absolute difference between column indexes is less than 3 else false.
   */
  def onBoard(squareFrom: Int, squareTo: Int) = squareTo >= 0 && squareTo < Board.NumSquares && math.abs(squareTo % 8 - squareFrom % 8) < 3

  private val pawnIncrements = Map(White -> List(8), Black -> List(-8))
  private val knightIncrements = List(10, 17, 15, 6, -10, -17, -15, -6)
  private val bishopIncrements = List(9, 7, -9, -7)
  private val rookIncrements = List(1, 8, -1, -8)
  private val queenIncrements = bishopIncrements ++ rookIncrements
  private val kingIncrements = queenIncrements

  private val moveLists: Array[Array[Array[List[List[Int]]]]] =
    Array.ofDim[List[List[Int]]](PieceColor.values.size, PieceType.values.size + 1, Board.NumSquares)

  for (side <- PieceColor.values; piece <- PieceType.values; fromSquare <- 0 until Board.NumSquares)
    moveLists(side.id)(piece.id)(fromSquare) = getMoveGenerator(Piece(piece, side), fromSquare).generate

  private def getMoveGenerator(piece: Piece, fromSquare: Int): StaticMoveGenerator = {
    val moveGenerator = StaticMoveGenerator(fromSquare) _
    piece match {
      case Piece(Pawn, side) if (Board.rank(fromSquare, side) == 0) => moveGenerator(Nil, 0)
      case Piece(Pawn, side) if (Board.rank(fromSquare, side) == 1) => moveGenerator(pawnIncrements(side), 2)
      case Piece(Pawn, side) => moveGenerator(pawnIncrements(side), 1)
      case Piece(Knight, _) => moveGenerator(knightIncrements, 1)
      case Piece(Bishop, _) => moveGenerator(bishopIncrements, 7)
      case Piece(Rook, _) => moveGenerator(rookIncrements, 7)
      case Piece(Queen, _) => moveGenerator(queenIncrements, 7)
      case Piece(King, _) => moveGenerator(kingIncrements, 1)
    }
  }

  private def apply(fromSquare: Int)(increments: List[Int], maxNumIncrements: Int) = new StaticMoveGenerator(fromSquare, increments, maxNumIncrements)

}

/**
 * Generates moves from the given square.
 * 
 * @param fromSquare is the originating square.
 * @param increments is a list of increments.
 * @param maxNumIncrements is the maximum number of increments in each direction.
 */
private class StaticMoveGenerator(fromSquare: Int, increments: List[Int], maxNumIncrements: Int) {
  
  /**
   * Generates the moves.
   * 
   * @return a list of lists containing the moves for each of the increments.
   */
  def generate: List[List[Int]] = {
    increments map { generateVector(_) } filter { !_.isEmpty }
  }

  /**
   * Generates a list of square indexes with the given increments.
   * 
   * @param increment is the difference between each of the returned square indexes.
   * @return a list of square indexes.
   */
  private def generateVector(increment: Int): List[Int] = {
    /**
     * Generate the next square index.
     * 
     * @param currentSquare is the current location.
     * @param count is the number of square indexes that have been generated so far.
     * @return a list of square indexes.
     */
    def generateVectorElement(currentSquare: Int, count: Int): List[Int] = {
      val nextSquare = currentSquare + increment
      if (count < maxNumIncrements && StaticMoveGenerator.onBoard(currentSquare, nextSquare))
          nextSquare :: generateVectorElement(nextSquare, count + 1)
      else Nil
    }
    generateVectorElement(fromSquare, 0)
  }
}
