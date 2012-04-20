package chess

import PieceType._
import PieceColor._
import BoardSide._

/**
 * Generates the move list.
 */
object DynamicMoveGenerator {
  
  /**
   * Gets the moves for the given position including any moves that would illegally leave the side making the move in check.
   * 
   * @param position is the current position.
   * @return a list of all legal moves.
   */
  def getMoveList(position: Position): List[Move] = {
    position.squareToPieceMap(position.sideToMove) flatMap {
      case (fromSquare, piece) => {
        DynamicMoveGenerator(position, fromSquare, piece).generate
      }
    } toList
  }
  
  /**
   * Creates a move generator.
   * 
   * @param position is the current position.
   * @param fromSquare is the index of the square to generate moves from.
   * @param piece is the piece to generate moves for.
   * @return the move generator.
   */
  def apply(position: Position, fromSquare: Int, piece: Piece) = piece match {
    case Piece(Pawn, White) => new PawnDynamicMoveGenerator(position, fromSquare, piece, List(7, 9))
    case Piece(Pawn, Black) => new PawnDynamicMoveGenerator(position, fromSquare, piece, List(-7, -9))
    case Piece(King, _) => new KingDynamicMoveGenerator(position, fromSquare, piece)
    case _ => new DynamicMoveGenerator(position, fromSquare, piece)
  }
}

/**
 * Generates moves for the given position.
 *
 * @param position is the current position.
 * @param fromSquare is the index of the square to generate moves from.
 * @param piece is the piece to generate moves for.
 */
class DynamicMoveGenerator(position: Position, fromSquare: Int, piece: Piece) {

  /** Contains the location of the opposing players pieces. */
  val opposingSquareToPieceMap = position.squareToPieceMap(PieceColor.otherColor(position.sideToMove))
  
  /**
   * Generates the move list.
   * 
   * @return the list of moves for this move generator.
   */
  def generate: List[Move] = {
    new PieceSquareNotifier[List[Move]](position, fromSquare, piece).generate(emptySquare, occupiedSquare) flatten
  }  
 
  /**
   * Callback notification of an empty square.
   * 
   * @param square is the empty square index.
   * @return a list of moves.
   */
  def emptySquare(square: Int): List[Move] = {
    createMoves(fromSquare, square, false)
  }
    
  /**
   * Callback notification of an empty square.
   * 
   * @param square is the occupied square index.
   * @param piece is the piece on the square.
   * @return a list of moves.
   */
  def occupiedSquare(square: Int, piece: Piece): List[Move] = {
    if (opposingSquareToPieceMap.contains(square))
      createMoves(fromSquare, square, true)
    else Nil
  }

  /**
   * Creates a list of moves.
   * 
   * Subclasses can override this method to customize move creation.
   * 
   * @param fromSquare is the source square index.
   * @param toSquare is the destination square index.
   * @param isCapture is true if the move is a capture else false.
   * @return a list of moves.
   */
  protected[this] def createMoves(fromSquare: Int, toSquare: Int, isCapture: Boolean): List[Move] = {
    List(BasicMove(fromSquare, toSquare, isCapture))
  }
}

/**
 * Generates king moves.
 * 
 * Includes castling moves.
 */
class KingDynamicMoveGenerator(position: Position, fromSquare: Int, piece: Piece) extends DynamicMoveGenerator(position, fromSquare, piece) {
  
  /**
   * Generates the move list.
   * 
   * @return the list of moves for this move generator.
   */
  override def generate: List[Move] = {
    super.generate ++ generateCastlingMoves
  }
  
  private def generateCastlingMoves = {    
    BoardSide.values.filter { position.canCastle(position.sideToMove, _) } flatMap { generateCastlingMove(_) }
  }
  
  private def generateCastlingMove(boardSide: BoardSide.Value): Option[Move] = {
    val castlingMetadata = CastlingMetadata.get(position.sideToMove)(boardSide)
    def nothingBetweenKingAndRook = castlingMetadata.squaresBetweenKingAndRook.find(!position.isEmpty(_)) == None
    def passingThroughCheck = SquareAttackFinder.isSquareAttacked(position, castlingMetadata.rookToSquare, position.opposingSide)
    def inCheck = SquareAttackFinder.isSquareAttacked(position, castlingMetadata.kingFromSquare, position.opposingSide)
    
    if (nothingBetweenKingAndRook && !passingThroughCheck && !inCheck) {
      Some(CastlingMove(castlingMetadata.kingFromSquare, castlingMetadata.kingToSquare, boardSide))
    } else None
  }
}

/**
 * Generates pawn moves.
 */
class PawnDynamicMoveGenerator(position: Position, fromSquare: Int, piece: Piece, captureIncrements: List[Int])
	extends DynamicMoveGenerator(position, fromSquare, piece) {

  /**
   * Generates the move list.
   * 
   * @return the list of moves for this move generator.
   */
  override def generate: List[Move] = {
    super.generate ++ generateStandardCaptures ++ generateEnPassant
  }
  
  /**
   * Overrides the base class implementation to prevent a pawn from making a forward capture.
   * 
   * @param square is the occupied square index.
   * @param piece is the piece on the square.
   * @return an empty list.
   */
  override def occupiedSquare(square: Int, piece: Piece): List[Move] = {
    Nil
  }
    
  /**
   * Overrides the base class implementation to return a list of promotion moves for a pawn that reaches the eighth rank.
   * 
   * @param fromSquare is the source square index.
   * @param toSquare is the destination square index.
   * @param isCapture is true if the move is a capture else false.
   * @return a list of moves.
   */
  override def createMoves(fromSquare: Int, toSquare: Int, isCapture: Boolean): List[Move] = {
    if (Board.rank(toSquare, piece.pieceColor) != 7) {
      super.createMoves(fromSquare, toSquare, isCapture)
    } else {
      List.range(Knight.id, King.id) map((id: Int) => PromotionMove(fromSquare, toSquare, isCapture, PieceType(id)))
    }
  }
  
  private def generateEnPassant = {
    position.enPassantSquare filter { Board.isAlongSide(_, fromSquare) } map { (epSquare: Int) => 
      EnPassantMove(fromSquare, epSquare + (if (piece.pieceColor == White) 8 else -8), epSquare)
    }
  }
  
  private def generateStandardCaptures = {
    captureIncrements map { _+fromSquare } filter {isValidCapture(_)} flatMap { createMoves(fromSquare, _, true) }
  }
  
  private def isValidCapture(toSquare: Int) = {
    opposingSquareToPieceMap.contains(toSquare) && StaticMoveGenerator.onBoard(fromSquare, toSquare) 
  }
}
