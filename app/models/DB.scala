package models

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection
import chess.Game
import chess.PieceColor._
import org.bson.types.ObjectId
import com.mongodb.util.JSON

/**
 * Persistence for a single game using MongoDB.
 */
object DB {
  
  private val objectID = 1
  private val mongoCollection = MongoConnection()("db")("games")
  
  /**
   * Gets the persisted game or a new game if it doesn't exist.
   * 
   * @return the game.
   */
  def get = {
    JsonGameParser.parseDB(JSON.serialize(getGameObject.getAs[Object]("game").get))
  }
  
  /**
   * Updates the persisted game.
   * 
   * @param game the updated game to persist.
   * @return the game.
   */
  def update(game: Game) = {
    mongoCollection.update(getGameObject, $set("game" -> JSON.parse(JsonGameSerializer.serializeDB(game))))
    game
  }

  private def getGameObject = {
     mongoCollection.findOne(DBObject("_id" -> objectID)).getOrElse(createGameObject)
  }
  
  private def createGameObject = {
    val gameObject = MongoDBObject("_id" -> objectID)
    gameObject += "game" -> JSON.parse(JsonGameSerializer.serializeDB(new Game))
    mongoCollection += gameObject
    gameObject
  }  

} 
