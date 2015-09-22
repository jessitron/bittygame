package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux.{Scenario, ScenarioTitle}
import com.jessitron.bittygame.web.identifiers.GameID
import com.jessitron.bittygame.web.messages.GameResponse
import com.jessitron.bittygame.web.ports.{TrivialGameStateDAO, TrivialScenarioDAO}
import org.scalatest.{Status, Args, Suite}
import spray.http.StatusCodes
import spray.testkit.ScalatestRouteTest

import spray.httpx.SprayJsonSupport._
import com.jessitron.bittygame.serialization._
import spray.json.DefaultJsonProtocol._

trait BittyGameServiceInfrastructure
  extends ScalatestRouteTest
  with Suite
  with BittyGameService {

  /* Call a route and parse its response */

  def callBeginGame(title: ScenarioTitle): GameResponse =
    callToTheFirstGameEndpoint(title) ~> myRoute ~> check {
      responseAs[GameResponse]
    }

  def callTakeTurn(gameID: GameID, move: String): GameResponse =
    callToTheTurnEndpoint(gameID, move) ~> myRoute ~> check {
      responseAs[GameResponse]
    }

  def callThink(gameID: GameID) =
    callToTheThinkEndpoint(gameID) ~> myRoute ~> check {
      responseAs[Seq[String]]
    }

  def create(scenario: Scenario) =
    callToTheCreateEndpoint(scenario) ~> myRoute ~> check {
      status == StatusCodes.OK
    }

  def encode(s: String) = java.net.URLEncoder.encode(s, "UTF-8")

  def callToTheFirstGameEndpoint(title: ScenarioTitle) =  Get(s"/scenario/${encode(title)}/begin")

  def callToTheThinkEndpoint(gameID: GameID) =  Get(s"/game/${encode(gameID)}/think")

  def callToTheTurnEndpoint(gameID: GameID, move: String) = Get(s"/game/${encode(gameID)}/turn/${encode(move)}")

  def callToTheCreateEndpoint(scenario: Scenario) = Put(s"/scenario/", scenario)

  abstract override def run(testName: Option[String], args: Args): Status = super.run(testName, args) // why is this here?

  def actorRefFactory = system
  val executionContext = scala.concurrent.ExecutionContext.global

  val scenarioDAO = new TrivialScenarioDAO()
  val gameStates = new TrivialGameStateDAO()
}
