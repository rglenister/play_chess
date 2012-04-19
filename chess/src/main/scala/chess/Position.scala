package chess


import scala.PartialFunction

import PieceType._
import PieceColor._
import BoardSide._


/**
 * Enumeration of the game statuses.
 */
object GameStatus extends Enumeration {
  val InProgress, DrawnByFiftyMoveRule, DrawnByThreefoldRepetition, Stalemate, TimeControl, Resignation, Checkmate = Value
}

trait Position {
  /**
   * Gets the square to piece map containing the positions of all the pieces.
   */
  def squareToPieceMap: Map[Int, Piece]
  
  /**
   * Gets the square to piece map for the given color.
   * 
   * @param color is the color of the pieces in the returned map.
   * @return the square to piece map.
   */
  def squareToPieceMap(color: PieceColor.Value): Map[Int, Piece] = squareToPieceMap filter { _._2.pieceColor == color }
  
  /**
   * Gets the inverse mapping of the square to piece map.
   * @return the inverse mapping.
   */
  lazy val pieceToSquaresMap: Map[Piece, Iterable[Int]] = squareToPieceMap groupBy {_._2} map {case (key,value) => (key, value.unzip._1)}
  
  /**
   * Gets the side to move.
   * @return the color of the side to move.
   */
  def sideToMove: PieceColor.Value
  
  /**
   * Gets the opponent of the side to move.
   * @return the opposing color.
   */
  def opposingSide = PieceColor.otherColor(sideToMove)
  
  /**
   * Determines if a square is empty.
   * 
   * @param square is the index of the square.
   * @return true if the square with the given index is empty else false.
   */
  def isEmpty(square: Int) = !squareToPieceMap.contains(square)
  
  /**
   * Gets the index of the destination square of the last move if it was a double square move by a pawn else None.
   */
  def enPassantSquare: Option[Int]
  
  /**
   * Tests whether castling is permitted.
   * 
   * @param color is the color of the side to test.
   * @param boardSide is the side of the board.
   * @return true if castling is permitted else false.
   */
  def canCastle(color: PieceColor.Value, boardSide: BoardSide.Value) = castlingRightsMap(color).canCastle(boardSide)
  
  /**
   * Gets the castling rights map.
   * 
   * @return a map containing the castling rights for both sides.
   */
  def castlingRightsMap: Map[PieceColor.Value, CastlingRights]
  
  /**
   * Gets the location of the king with the given color.
   * 
   * @param color is the color of the king.
   * @return the index of the square containing the king.
   */
  def getKingSquare(color: PieceColor.Value): Int = squareToPieceMap.find(_._2 == Piece(King, color)).get._1 

  /**
   * @return the count of half moves since the last irreversible move was made in accordance with the 50 move rule.
   */
  def fiftyMoveRuleCount: Int
  
  /**
   * @return the full move number.
   */
  def fullMoveNumber: Int
  
  /**
   * @return a list of all previous positions in reverse chronological order.
   */
  def previousPositions: List[Position]
  
  /**
   * @return a list of all legal moves from this position.
   */
  lazy val moveList: List[Move] = DynamicMoveGenerator.getMoveList(this).filter(GamePosition(this, _) != None)

  /** true if the side to move is in check else false. */
  lazy val isCheck = checkCount > 0
  
  /** true if the side to move is in check else false. */
  lazy val checkCount = SquareAttackFinder.findAttackingSquares(this, getKingSquare(sideToMove), opposingSide).length

  /** The game status. */
  lazy val gameStatus = (moveList.isEmpty, isCheck) match {
    case (true, true) => GameStatus.Checkmate
    case (true, false) => GameStatus.Stalemate
    case _ =>  GameStatus.InProgress
  }
  
}

/**
 * Factory for game positions.
 */
object GamePosition {
  
  /**
   * Creates a position for a new game.
   * 
   * @return the new game position.
   */
  def apply(): GamePosition = {
	GamePosition(
      Board.startingPosition,
      White,
      CastlingRights.create(Board.startingPosition)
    )    
  }
  
  /**
   * Creates a position with the given attributes.
   * 
   * @see GamePosition
   */
  def apply(
    squareToPieceMap: Map[Int, Piece],
    sideToMove: PieceColor.Value,
    castlingRightsMap: Map[PieceColor.Value, CastlingRights],
    enPassantSquare: Option[Int] = None,
    fiftyMoveRuleCount: Int = 0,
    fullMoveNumber: Int = 1,
    previousPositions: List[Position] = List()      
  ): GamePosition = {
    new GamePosition(
      squareToPieceMap: Map[Int, Piece],
      sideToMove: PieceColor.Value,
      castlingRightsMap: Map[PieceColor.Value, CastlingRights],
      enPassantSquare: Option[Int],
      fiftyMoveRuleCount,
      fullMoveNumber,
      previousPositions      
    )
  }
  
  /**
   * Creates the next position.
   * 
   * @param previousPosition is the position preceding this position.
   * @param move is the move that was made on the previous position.
   * @return the new game position.
   */
  def apply(previousPosition: Position, move: Move): Option[GamePosition] = {
    val squareToPieceMap = createSquareToPieceMap(previousPosition, move)
    val position = new GamePosition(
      squareToPieceMap,
      previousPosition.opposingSide,
      CastlingRights.next(squareToPieceMap, previousPosition.castlingRightsMap),
      createEnPassantSquare(previousPosition, move),
      createFiftyMoveRuleCount(previousPosition, move),
      previousPosition.fullMoveNumber + (if (previousPosition.opposingSide == White) 1 else 0),
      previousPosition :: previousPosition.previousPositions
    )
    if (!SquareAttackFinder.isSquareAttacked(position, position.getKingSquare(position.opposingSide), position.sideToMove)) {
      Some(position)
    } else None
  }

  private def movePiece(nextSquareToPieceMap: Map[Int, Piece], fromSquare: Int, toSquare: Int): Map[Int, Piece] = {
    nextSquareToPieceMap - fromSquare + (toSquare -> nextSquareToPieceMap(fromSquare))
  }
  
  private def createSquareToPieceMap(previousPosition: Position, move: Move): Map[Int, Piece] = {
    
    val enPassantMove: PartialFunction[Move, Map[Int, Piece] => Map[Int, Piece]] = {
      case enPassantMove: EnPassantMove => { squareToPieceMap => squareToPieceMap - previousPosition.enPassantSquare.get }
    }
    
    val castlingMove: PartialFunction[Move, Map[Int, Piece] => Map[Int, Piece]] = {
      case CastlingMove(_, _, boardSide) => { squareToPieceMap =>
        val metadata = CastlingMetadata.get(previousPosition.sideToMove)(boardSide)
        movePiece(squareToPieceMap, metadata.rookFromSquare, metadata.rookToSquare)
      }
    }
    
    val promotionMove: PartialFunction[Move, Map[Int, Piece] => Map[Int, Piece]] = {
      case PromotionMove(_, toSquare, _, toPiece) => { squareToPieceMap =>
        squareToPieceMap + (toSquare -> Piece(toPiece, previousPosition.sideToMove)) 
      }
    }
    
    val basicMove: PartialFunction[Move, Map[Int, Piece] => Map[Int, Piece]] = {
      case basicMove: BasicMove => { squareToPieceMap => squareToPieceMap }
    }
    
    (enPassantMove orElse castlingMove orElse promotionMove orElse basicMove)(move)(
      movePiece(previousPosition.squareToPieceMap, move.fromSquare, move.toSquare))
  }
  
  private def createEnPassantSquare(previousPosition: Position, move: Move): Option[Int] = {
    if (previousPosition.squareToPieceMap(move.fromSquare).pieceType == Pawn && move.distance == 2)
      Some(move.toSquare)
    else
      None    
  }
  
  private def createFiftyMoveRuleCount(previousPosition: Position, move: Move) = {
    if (move.isCapture || previousPosition.squareToPieceMap(move.fromSquare).pieceType == Pawn || move.isInstanceOf[CastlingMove]) {
      0
    } else previousPosition.fiftyMoveRuleCount + 1
  }
}

/**
 * Concrete implementation of the position trait.
 * 
 * @param squareToPieceMap contains the piece locations.
 * @param sideToMove is the side to move.
 * @param castlingRightsMap contains the castling rights for each player.
 * @param enPassantSquare is the destination square of the last move if it was a double square move by a pawn otherwise None.
 * @param fiftyMoveRuleCount is the count of half moves in accordance with the fifty move rule.
 * @param fullMoveNumber is the full move move number.
 * @param previousPositions contains all previous game positions.
 */
class GamePosition(
    val squareToPieceMap: Map[Int, Piece],
    val sideToMove: PieceColor.Value,
    val castlingRightsMap: Map[PieceColor.Value, CastlingRights],
    val enPassantSquare: Option[Int],
    val fiftyMoveRuleCount: Int,
    val fullMoveNumber: Int,
    val previousPositions: List[Position]
    ) extends Position {

  /**
   * Gets the hash code of this game position.
   * 
   * @return the hash code.
   */
  override def hashCode: Int = {
    41 * (
      41 * (
        41 + squareToPieceMap.hashCode
      ) + castlingRightsMap.hashCode
    ) + enPassantSquare.hashCode
  }
  
  /**
   * Equals operator.
   * 
   * Game positions are considered equal if their pieces are on the same squares and both have equal castling
   * rights and en passant squares. This is in accordance with the threefold repetition rule.
   * 
   * @param other is the object to test.
   * @return true if this game position is equal to the supplied object else false.
   */
  override def equals(other: Any) = other match {
    case that: GamePosition => (that canEqual this) && 
                               this.squareToPieceMap == that.squareToPieceMap &&
                               this.castlingRightsMap == that.castlingRightsMap &&
                               this.enPassantSquare == that.enPassantSquare
    case _ => false
  }
  
  /**
   * Tests whether other is a game position.
   * 
   * @param other is the object to test.
   * @return true if other is a game position.
   */
  def canEqual(other: Any) = other.isInstanceOf[GamePosition]
  
  override def toString = {
    (7 to 0 by -1).foldLeft("") { (resultString, row) =>
      resultString + (0 to 7).foldLeft("") { (rowString, col) =>
    	rowString + "%s ".format(squareToPieceMap.get(row*8+col).map { _.toChar.toString } getOrElse("-"))
      } + "\n"
    }
  }
  
  /**
   * Determines how many times this game position has been repeated.
   * 
   * @return the repetition count of this game position.
   */
  lazy val repetitionOfPositionCount: Int = {
    previousPositions takeWhile { _.fiftyMoveRuleCount >= 0 } count { _ == this }
  }
  
}
