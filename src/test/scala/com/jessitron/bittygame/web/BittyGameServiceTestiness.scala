package com.jessitron.bittygame.web

import com.jessitron.bittygame.web.ports.{TrivialGameStateDAO, TrivialScenarioDAO}
import org.scalatest.{Status, Args, Suite}
import spray.testkit.ScalatestRouteTest

trait BittyGameServiceTestiness
  extends ScalatestRouteTest
  with Suite
  with BittyGameService {

  abstract override def run(testName: Option[String], args: Args): Status = super.run(testName, args)

  def actorRefFactory = system
  val executionContext = scala.concurrent.ExecutionContext.global

  val scenarioDAO = new TrivialScenarioDAO()
  val gameStates = new TrivialGameStateDAO()
}
