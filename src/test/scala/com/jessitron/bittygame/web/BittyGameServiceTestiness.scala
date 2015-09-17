package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux.ScenarioTitle
import com.jessitron.bittygame.web.identifiers.GameID
import com.jessitron.bittygame.web.ports.{TrivialGameStateDAO, TrivialScenarioDAO}
import org.scalatest.{Status, Args, Suite}
import spray.testkit.ScalatestRouteTest

trait BittyGameServiceTestiness
  extends ScalatestRouteTest
  with Suite
  with BittyGameService {

  private def encode(s: String) = java.net.URLEncoder.encode(s, "UTF-8")

  def callToTheFirstGameEndpoint(title: ScenarioTitle) =  Get(s"/scenario/${encode(title)}/begin")

  def callToTheThinkEndpoint(gameID: GameID) =  Get(s"/game/${encode(gameID)}/think")

  def callToTheTurnEndpoint(gameID: GameID, move: String) = Get(s"/game/${encode(gameID)}/turn/${encode(move)}")

  abstract override def run(testName: Option[String], args: Args): Status = super.run(testName, args)

  def actorRefFactory = system
  val executionContext = scala.concurrent.ExecutionContext.global

  val scenarioDAO = new TrivialScenarioDAO()
  val gameStates = new TrivialGameStateDAO()
}
