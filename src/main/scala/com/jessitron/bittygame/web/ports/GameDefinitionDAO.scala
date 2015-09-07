package com.jessitron.bittygame.web.ports

import com.jessitron.bittygame.web.ports.GameDefinitionDAO._

import scala.collection.immutable.HashMap
import scala.concurrent.Future
import com.jessitron.bittygame.crux.GameDefinition

// this could hook up to another service, or a database, or be a simple cache
trait GameDefinitionDAO {
  import GameDefinitionDAO._
  def save(name: GameDefinitionKey, gameDef: GameDefinition): Future[SaveResult]
  def retrieve(name: GameDefinitionKey): Future[GameDefinition]
}

object GameDefinitionDAO {
  type GameDefinitionKey = String
  type SaveResult = Unit
}

class TrivialGameDefinitionDAO extends GameDefinitionDAO {
    var gameDefCache = HashMap[GameDefinitionKey, GameDefinition]()
    override def retrieve(name: GameDefinitionKey): Future[GameDefinition] =
      gameDefCache.get(name) match {
        case Some(d) => Future.successful(d)
        case None => Future.failed(new RuntimeException(s"Undefined game: $name"))
      }
    override def save(name: GameDefinitionKey, gameDef: GameDefinition): Future[SaveResult] = {
      gameDefCache = gameDefCache + (name -> gameDef)
      Future.successful(())
    }
}
