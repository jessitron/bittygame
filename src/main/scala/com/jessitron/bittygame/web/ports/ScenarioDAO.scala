package com.jessitron.bittygame.web.ports

import com.jessitron.bittygame.web.identifiers.ScenarioKey
import com.jessitron.bittygame.web.ports.ScenarioDAO._

import scala.collection.immutable.HashMap
import scala.concurrent.Future
import com.jessitron.bittygame.crux.Scenario

// this could hook up to another service, or a database, or be a simple cache
trait ScenarioDAO {
  import ScenarioDAO._
  def save(name: ScenarioKey, scenario: Scenario): Future[SaveResult]
  def retrieve(name: ScenarioKey): Future[Scenario]
  def names(): Seq[ScenarioKey]
}

object ScenarioDAO {
  type SaveResult = Unit
  class NotFoundException(msg: String) extends Exception(msg)
}

class TrivialScenarioDAO extends ScenarioDAO {
  var scenarioCache = HashMap[ScenarioKey, Scenario]()
  override def retrieve(name: ScenarioKey): Future[Scenario] =
    scenarioCache.get(name) match {
      case Some(d) => Future.successful(d)
      case None => Future.failed(new ScenarioDAO.NotFoundException(s"Undefined game: $name"))
    }
  override def save(name: ScenarioKey, scenario: Scenario): Future[SaveResult] = {
    scenarioCache = scenarioCache + (name -> scenario)
    Future.successful(())
  }

  override def names(): Seq[ScenarioKey] = scenarioCache.keys.toSeq
}

