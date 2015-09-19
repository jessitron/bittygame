package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux._
import com.jessitron.bittygame.gen.ScenarioGen
import com.jessitron.bittygame.web.messages.GameResponse
import org.scalatest.ShouldMatchers
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import spray.http._
import spray.httpx.SprayJsonSupport._
import com.jessitron.bittygame.serialization._
import StatusCodes._

class BittyGameServiceSpec extends org.scalatest.PropSpec
                       with GeneratorDrivenPropertyChecks
                       with ShouldMatchers
                       with BittyGameServiceTestiness
                       with ScenarioGen {

  property ("the first turn prints the welcome message") {
    forAll(opportunitiesGen,
           welcomeMessageGen,
           scenarioTitleGen)
    { (someActions: Seq[Opportunity],
       message: MessageToThePlayer,
        title: ScenarioTitle) =>

      val someGame = Scenario(title, someActions, message)

      Put("/scenario/", someGame) ~> myRoute ~> check {
        status should be(Created)
      }

      Get("/scenario/" + encode(title) + "/begin") ~> myRoute ~> check {
        responseAs[GameResponse].instructions should contain(Print(message))
      }
    }
  }
}
