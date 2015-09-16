package com.jessitron.bittygame.web.ports

import java.util.UUID

import com.jessitron.bittygame.crux.GameState
import com.jessitron.bittygame.web.identifiers.GameID

import scala.collection.mutable
import scala.concurrent.Future

object GameStateDAO {
  case class SaveResult(gameID: GameID)
  type ForgetResult = Unit
  class NotFoundException(msg: String) extends Exception(msg)



  def newGameID(): GameID = UUID.randomUUID().toString
}

trait GameStateDAO {
  import GameStateDAO._
  def store(gameState: GameState) : Future[SaveResult]
  def update(gameID: GameID, gameState: GameState) : Future[SaveResult]
  def recall(gameID: GameID) : Future[GameState]
  def forget(gameID: GameID) : Future[ForgetResult]
}


class TrivialGameStateDAO extends GameStateDAO {
  import GameStateDAO._

  private val states = new mutable.HashMap[GameID, GameState]


  override def store(gameState: GameState): Future[SaveResult] = {
    val gameID = newGameID()
    states.put(gameID, gameState)
    Future.successful(SaveResult(gameID)) // single threaded
  }

  override def update(gameID: GameID, gameState: GameState): Future[SaveResult] = {
    states.put(gameID, gameState)
    Future.successful(SaveResult(gameID)) // single threaded
  }

  override def recall(gameID: GameID): Future[GameState] = {
    states.get(gameID) match {
      case Some(state) => Future.successful(state)
      case None => Future.failed(new NotFoundException("No saved state for game: " + gameID))
    }
  }

  override def forget(gameID: GameID): Future[ForgetResult] = {
    states.remove(gameID)
    Future.successful(())
  }
}
