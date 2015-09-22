package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux.{Scenario, Print}
import com.jessitron.bittygame.gen.ScenarioGen
import com.jessitron.bittygame.scenarios.JessLife
import com.jessitron.bittygame.web.messages.GameResponse
import org.scalatest.ShouldMatchers
import org.scalatest.exceptions.GeneratorDrivenPropertyCheckFailedException
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import spray.http._
import spray.httpx.SprayJsonSupport._
import com.jessitron.bittygame.serialization._
import spray.json.DefaultJsonProtocol._
import StatusCodes._

class BittyGameServiceSpec1 extends org.scalatest.FunSpec
with ShouldMatchers
with BittyGameServiceInfrastructure
with GeneratorDrivenPropertyChecks
with ScenarioGen {

  describe ("the first turn") {

    it("prints the welcome message") {

      val message: String = "Yo Yo Yo!"
      val temp = JessLife.scenario
      assertCreateAndBegin(message, temp)
    }
  }

  forAll(welcomeMessageGen, scenarioGen) (assertCreateAndBegin)

  def assertCreateAndBegin(message: String, temp: Scenario): Unit = {
    val someScenario = temp.copy(welcome = message)

    Put("/scenario/", someScenario) ~> myRoute ~> check {
      status should be(Created)
    }

    Get("/scenario/" + encode(someScenario.title) + "/begin") ~> myRoute ~> check {
      responseAs[GameResponse].instructions should contain(Print(message))
    }
  }
}
