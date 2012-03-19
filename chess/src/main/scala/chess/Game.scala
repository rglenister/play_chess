package chess


import scala.collection.immutable.ListMap

import PieceColor._

/**
 * Contains the details of a player.
 */
case class Player(name: String)

/**
 * Contains a move and its resulting position.
 */
case class GameMove( move: Move, position: Position)

/**
 * Game object.
 */
object Game {
  
  private val defaultPlayersMap = Map(White -> Player("White Player"), Black -> Player("Black Player"))
}

/**
 * Defines a game.
 */
class Game(
    val firstPosition: Position,
    val moveMap: Map[Int, GameMove] = ListMap(),
    val nextMoveMapIndex: Int,
    val players: Map[PieceColor.Value, Player] = Game.defaultPlayersMap) {
  
  /**
   * Construct a game using the given position.
   * 
   * @param position is the initial position of the game.
   */
  def this(position: Position) = {
    this(position, ListMap(), 0, Game.defaultPlayersMap)
  }
  
  /**
   * Construct a game with the standard starting position. 
   */
  def this() = {
    this(GamePosition())
  } 
  
  /** The current position of this game. */
  val currentPosition = if (nextMoveMapIndex>0) moveMap(nextMoveMapIndex-1).position else firstPosition
  
  /**
   * Makes a move.
   * 
   * The list of legal moves for this game is searched for the move matching the given parameters and if found the
   * move is made and a Some new game is returned otherwise None
   * 
   * @param fromSquare is the source square.
   * @param toSquare is the to square.
   * @param promoteTo contains the piece to promote to or is None if the move is not a pawn promotion.
   * @return a new game with the added move or None if the move was illegal.
   */
  def makeMove(fromSquare: Int, toSquare: Int, promoteTo: Option[PieceType.Value] = None): Option[Game] = {
    currentPosition.moveList find {
      case m: Move if (m.fromSquare != fromSquare || m.toSquare != toSquare) => false
      case PromotionMove(_, _, _, toPiece) if (promoteTo.isDefined && promoteTo.get==toPiece) => true
      case PromotionMove(_, _, _, _) => false
      case _: Move => true
    } flatMap { makeMove(_) }
  }
  
  /**
   * Makes a move.
   * 
   * No check is made to see if the given move is legal on this game.
   * 
   * @param move is the move to make.
   * @return Some containing the new game with the added move.
   */
  def makeMove(move: Move): Option[Game] = {
    val nextPosition = GamePosition(currentPosition, move).get
    val gameMove = GameMove(move, nextPosition)
    val nextMoveMap = moveMap.dropRight(moveMap.size - nextMoveMapIndex) + (nextMoveMapIndex -> gameMove)
    Some(new Game(firstPosition, nextMoveMap, nextMoveMapIndex+1, players))
  }
  
  /**
   * Goes to the previous position.
   * 
   * @return a game with current position equal to the previous position of this game or None if this is the first position.
   */
  def previousPosition: Option[Game] = {
    setCurrentPosition(nextMoveMapIndex - 1)
  }
  
  /**
   * Goes to the next position.
   * 
   * @return a game with current position equal to the next position of this game or None if this is the last position.
   */
  def nextPosition: Option[Game] = {
    setCurrentPosition(nextMoveMapIndex + 1)
  }
  
  /**
   * Goes to the position with the given index.
   */
  def setCurrentPosition(moveMapIndex: Int): Option[Game] = {
    if (moveMapIndex>=0 && moveMapIndex<=moveMap.size) {
      Some(new Game(firstPosition, moveMap, moveMapIndex, players))
    } else None
  }
  
  /**
   * 
   */
  def setPlayer(color: PieceColor.Value, player: Player): Game = {
    new Game(firstPosition, moveMap, nextMoveMapIndex, players + (color -> player))
  }
  
}