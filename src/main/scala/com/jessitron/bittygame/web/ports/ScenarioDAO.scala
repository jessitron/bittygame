package com.jessitron.bittygame.web.ports

import com.jessitron.bittygame.web.ports.ScenarioDAO._

import scala.collection.immutable.HashMap
import scala.concurrent.Future
import com.jessitron.bittygame.crux.{ScenarioTitle, Scenario}

// this could hook up to another service, or a database, or be a simple cache
trait ScenarioDAO {
  import ScenarioDAO._
  def save(name: ScenarioTitle, scenario: Scenario): Future[SaveResult]
  def retrieve(name: ScenarioTitle): Future[Scenario]
  def names(): Seq[ScenarioTitle]
}

object ScenarioDAO {
  type SaveResult = Unit
  class NotFoundException(msg: String) extends Exception(msg)
}

class TrivialScenarioDAO extends ScenarioDAO {
  var scenarioCache = HashMap[ScenarioTitle, Scenario]()
  override def retrieve(name: ScenarioTitle): Future[Scenario] =
    scenarioCache.get(name) match {
      case Some(d) => Future.successful(d)
      case None => Future.failed(new ScenarioDAO.NotFoundException(s"Undefined game: $name"))
    }
  override def save(name: ScenarioTitle, scenario: Scenario): Future[SaveResult] = {
    scenarioCache = scenarioCache + (name -> scenario)
    Future.successful(())
  }

  override def names(): Seq[ScenarioTitle] = scenarioCache.keys.toSeq
}

