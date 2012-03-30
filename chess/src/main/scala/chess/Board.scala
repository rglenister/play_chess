package chess


/**
 * Enumeration of the piece types.
 */
object PieceType extends Enumeration {
  val Pawn = Value(1)
  val Knight, Bishop, Rook, Queen, King = Value
  
  /**
   * Gets a character representation of the piece type using its initial character.
   * 
   * @param pieceType is the type of the piece.
   * @return the first letter of the type name.
   */
  def toChar(pieceType: PieceType.Value) = if (pieceType == Knight) 'N' else pieceType.toString.head
  
  /**
   * Finds the piece type with the given single character representation.
   * 
   * @param p is the character representation of the piece. It is case insensitive.
   * @return some piece type or none if no piece is represented by the given character.
   */
  def fromChar(p: Char): Option[PieceType.Value] = {
    val up = p.toUpper
    if (up=='N') Some(Knight)
    else values find { _.toString.head == up }
  }
}

/**
 * Enumeration of the piece colors.
 */
object PieceColor extends Enumeration {
  val White, Black = Value
  
  def otherColor(color: Value) = PieceColor(1 - color.id)
}

case class Piece(pieceType:PieceType.Value, pieceColor:PieceColor.Value) {

  /**
   * Gets a character representation of this piece.
   * 
   * @return the first letter of the piece type name. If this piece is black the returned value is converted to lower case.
   */
  def toChar = {
    val ch = PieceType.toChar(pieceType)
    if (pieceColor == PieceColor.White) ch
    else ch toLower
  }
}

/**
 * Enumeration of the board sides.
 */
object BoardSide extends Enumeration {
  val Kingside, Queenside = Value
}


import PieceColor._
import PieceType._
import BoardSide._


/**
 * Conversions 
 */
object implicits {
  implicit def intToPiece(i:Int) = Piece(PieceType(math.abs(i)), if (i>0) PieceColor.White else PieceColor.Black)
  implicit def pieceToInt(piece:Piece) = if (piece.pieceColor == PieceColor.White) piece.pieceType.id else -piece.pieceType.id
}

/**
 * Board object.
 */
object Board {

  /** The number of squares on the board. */
  val NumSquares = 64
    
  /**
   * Gets the zero based row of the given square.
   * 
   * @param square is the square.
   * @return the row.
   */
  def row(square: Int) = square/8
  
  /**
   * Gets the zero based column of the given square.
   * 
   * @param square is the square.
   * @return the column.
   */
  def column(square: Int) = square%8
  
  /**
   * Gets the zero based rank of the given square.
   * 
   * @param square is the square.
   * @param side is the color of the side.
   * @return the rank.
   */
  def rank(square: Int, side:PieceColor.Value) = if (side==PieceColor.White) row(square) else 7 - row(square) 
  
  /**
   * Determines whether the given squares are next to each other on the same row.
   * 
   * @param square1 is the first square.
   * @param square2 is the second square.
   * @return true if the squares are alongside each other else false.
   */
  def isAlongSide(square1: Int, square2: Int) = math.abs(square2-square1) == 1 && row(square1) == row(square2)
  
  /**
   * Gets the distance between the given squares i.e. the maximum of their absolute difference in row and column respectively.
   * 
   * @param square1 is the first square.
   * @param square2 is the second square.
   * @return the distance between the two squares.
   */
  def distance(square1: Int, square2: Int) = math.max(math.abs(row(square2)-row(square1)), math.abs(column(square2)-column(square1)))
  
  /**
   * A square to piece map containing the new game position.
   */
  val startingPosition: Map[Int, Piece] = {
    Map(
      0 -> Piece(Rook, White), 7 -> Piece(Rook, White),
      1 -> Piece(Knight, White), 6 -> Piece(Knight, White),
      2 -> Piece(Bishop, White), 5 -> Piece(Bishop, White),
      3 -> Piece(Queen, White),
      4 -> Piece(King, White),
      8 -> Piece(Pawn, White), 9 -> Piece(Pawn, White), 10 -> Piece(Pawn, White), 11 -> Piece(Pawn, White), 
      12 -> Piece(Pawn, White), 13 -> Piece(Pawn, White), 14 -> Piece(Pawn, White), 15 -> Piece(Pawn, White),
      56 -> Piece(Rook, Black), 63 -> Piece(Rook, Black),
      57 -> Piece(Knight, Black), 62 -> Piece(Knight, Black),
      58 -> Piece(Bishop, Black), 61 -> Piece(Bishop, Black),
      59 -> Piece(Queen, Black),
      60 -> Piece(King, Black),
      48 -> Piece(Pawn, Black), 49 -> Piece(Pawn, Black), 50 -> Piece(Pawn, Black), 51 -> Piece(Pawn, Black), 
      52 -> Piece(Pawn, Black), 53 -> Piece(Pawn, Black), 54 -> Piece(Pawn, Black), 55 -> Piece(Pawn, Black))
  }
  
  def algebraicToSquareIndex(algebraic: String): Int = {
    val Algebraic = """^([a-hA-H])([1-8])$""".r
    val Algebraic(letter, digit) = algebraic
    8 * (digit.toInt-1) + (7 - ('h' - letter(0)))
  }
  
  def squareIndexToAlgebraic(square: Int) = {
    "%c%d".format('a' + column(square), row(square) + 1)
  }
  
  /**
   * Gets the king's home square.
   * 
   * @param color is the color of the king.
   * @return the square index of the king.
   */
  def getKingHomeSquare(color: PieceColor.Value) = kingHomeSquares(color.id)
  
  /**
   * Gets the increments to be added to a pawns current location to find the (up to) two squares it is attacking.
   * 
   * @param color is the color of the pawn.
   * @return the increments.
   */
  def getPawnAttackSquares(color: PieceColor.Value) = pawnAttackIncrements(color.id)
  
  private val pawnAttackIncrements = Array.ofDim[List[Int]](2)
  pawnAttackIncrements(White.id) = List(7, 9)
  pawnAttackIncrements(Black.id) = List(-7, -9)

  private val kingHomeSquares = Array.ofDim[Int](2)
  kingHomeSquares(White.id) = 4
  kingHomeSquares(Black.id) = 60
  
}
