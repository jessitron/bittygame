package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux.ScenarioTitle
import com.jessitron.bittygame.web.ports.{TrivialGameStateDAO, TrivialScenarioDAO}
import org.scalatest.{Status, Args, Suite}
import spray.testkit.ScalatestRouteTest

trait BittyGameServiceTestiness
  extends ScalatestRouteTest
  with Suite
  with BittyGameService {

  def callToTheFirstGameEndpoint(title: ScenarioTitle) =  Get(s"/scenario/${java.net.URLEncoder.encode(title, "UTF-8")}/begin")

  abstract override def run(testName: Option[String], args: Args): Status = super.run(testName, args)

  def actorRefFactory = system
  val executionContext = scala.concurrent.ExecutionContext.global

  val scenarioDAO = new TrivialScenarioDAO()
  val gameStates = new TrivialGameStateDAO()
}
