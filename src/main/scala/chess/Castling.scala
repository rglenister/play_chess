package chess

import PieceType._
import PieceColor._
import BoardSide._

/**
 * Factory producing a map of castling rights.
 */
object CastlingRights {

  private val newGameCastlingRights = CastlingRights(false, Map(Kingside -> false, Queenside -> false))
  private val newGameCastlingRightsMap = create(newGameCastlingRights, newGameCastlingRights)
  
  /**
   * Creates a map containing the castling rights of both players.
   * 
   * A player is assumed to have castling rights if the king and rook are on their home squares.
   * 
   * @param squareToPieceMap contains the piece locations.
   * @return the castling rights.
   */
  def create(squareToPieceMap: Map[Int, Piece]): Map[PieceColor.Value, CastlingRights] = {
    next(squareToPieceMap, newGameCastlingRightsMap)
  }
  
  /**
   * Creates a castling rights map.
   * 
   * @param white is whites castling rights.
   * @param black is blacks castling rights.
   * @return a map containing the given castling rights.
   */
  def create(white: CastlingRights, black: CastlingRights): Map[PieceColor.Value, CastlingRights] = {
    Map(White -> white, Black -> black)
  }
  
  /**
   * Creates a castling rights map.
   * 
   * @param fen is a FEN fragment containing the white and black castling rights.
   * @return a map containing the castling rights from the given fen fragment.
   */
  def create(fen: String): Map[PieceColor.Value, CastlingRights] = {
    val white = CastlingRights(false, Map(Kingside -> !fen.contains("K"), Queenside -> !fen.contains("Q")))
    val black = CastlingRights(false, Map(Kingside -> !fen.contains("k"), Queenside -> !fen.contains("q")))
    create(white, black)
  }
  
  /**
   * Creates the next castling rights.
   * 
   * @param squareToPieceMap contains the piece locations.
   * @param previous contains the castling rights of the previous position.
   * @return the castling rights.
   */
  def next(squareToPieceMap: Map[Int, Piece], previous: Map[PieceColor.Value, CastlingRights] = newGameCastlingRightsMap)
    : Map[PieceColor.Value, CastlingRights] = {
    
    /**
     * Gets the next castling rights for the given color.
     * 
     * @param color is the piece color.
     * @param previous is the castling rights of the previous position.
     * @return the castling rights.
     */
    def nextCastlingRights(color: PieceColor.Value, previous: CastlingRights) = {
	  CastlingRights(
	    previous.kingHasMoved || !squareContainsPiece(Board.getKingHomeSquare(color), Piece(King, color)),
	    previous.rookHasMoved map {
	      case (boardSide, rookHasMoved) => {
	        (boardSide, rookHasMoved || !squareContainsPiece(CastlingMetadata.get(color)(boardSide).rookFromSquare, Piece(Rook, color)))
	      }
	    }
	  )
	}
    
    /**
     * Determines if the given square contains the given piece.
     * 
     * @param square is the square.
     * @param piece is the piece.
     * @return true if the square contains the piece else false.
     */
    def squareContainsPiece(square: Int, piece: Piece) = {
      squareToPieceMap.get(square) == Some(piece)
    }
    
    previous map {
      case (color, castlingRights) => (color, nextCastlingRights(color, castlingRights))
    }
  }
}

/**
 * The castling rights class.
 * 
 * @param kingHasMoved is true if the king has moved else false.
 * @param rookHasMoved is a map containing rook moved booleans for each of the two rooks.
 */
case class CastlingRights(kingHasMoved: Boolean, rookHasMoved: Map[BoardSide.Value, Boolean]) {
  
  /**
   * Determines if there is a right to castle on the given side of the board.
   * 
   * @param boardSide is the side of the board.
   * @return true if if there is a right to castle else false.
   */
  def canCastle(boardSide: BoardSide.Value): Boolean = !kingHasMoved && !rookHasMoved(boardSide)

}

/**
 * Castling metadata contains king and rook locations for castling.
 */
object CastlingMetadata {

  /**
   * Gets the castling metadata.
   * 
   * @param pieceColor is the color of the side for which the metadata is required.
   * @return the castling metadata.
   */
  def get(color: PieceColor.Value) = {
    metadata(color)
  }
  
  private val metadata = Map(
    White -> Map(Kingside -> CastlingMetadata(4, 6, 7, 5), Queenside -> CastlingMetadata(4, 2, 0, 3)),    
    Black -> Map(Kingside -> CastlingMetadata(60, 62, 63, 61), Queenside -> CastlingMetadata(60, 58, 56, 59))    
  )
}

/**
 * The castling metadata class.
 */
case class CastlingMetadata (kingFromSquare: Int, kingToSquare: Int, rookFromSquare: Int, rookToSquare: Int) {
  def squaresBetweenKingAndRook = rookToSquare until rookFromSquare by (kingToSquare-rookToSquare)  
}
