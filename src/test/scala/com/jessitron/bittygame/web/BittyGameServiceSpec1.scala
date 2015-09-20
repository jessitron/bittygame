package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux.{Scenario, Print}
import com.jessitron.bittygame.gen.ScenarioGen
import com.jessitron.bittygame.web.messages.GameResponse
import org.scalacheck.Prop
import org.scalatest.ShouldMatchers
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import spray.http._
import spray.httpx.SprayJsonSupport._
import com.jessitron.bittygame.serialization._
import spray.json.DefaultJsonProtocol._
import StatusCodes._

class BittyGameServiceSpec1 extends org.scalatest.PropSpec
with GeneratorDrivenPropertyChecks
with ShouldMatchers
with BittyGameServiceInfrastructure
with ScenarioGen {
   property("it prints the welcome message") {
     forAll { (welcome: String,
               scenario: Scenario) =>
       whenever(welcome.nonEmpty) {

         val someScenario = scenario.copy(welcome = welcome)

         Put("/scenario/", someScenario) ~> myRoute ~> check {
           status should be(Created)
         }

         Get("/scenario/" + encode(someScenario.title) + "/begin") ~> myRoute ~> check {
           responseAs[GameResponse].instructions should contain(Print(welcome))
         }
         println("it wnet")
       }
     }
   }
}
