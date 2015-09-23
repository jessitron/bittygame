package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux.{Scenario, Print}
import com.jessitron.bittygame.web.messages.GameResponse
import org.scalatest.ShouldMatchers
import spray.http._
import spray.httpx.SprayJsonSupport._
import com.jessitron.bittygame.serialization._
import spray.json.DefaultJsonProtocol._
import StatusCodes._

class BittyGameServiceSpec1 extends org.scalatest.FunSpec
with ShouldMatchers
with BittyGameServiceInfrastructure {

  describe ("the first turn") {

    it("prints the welcome message") {

      val someScenario = Scenario(title = "yoto",
                                  welcome = "Why hello there",
                                  opportunities = Seq(),
                                  stats = Seq(),
                                  items = Seq())
      Put("/scenario/", someScenario) ~> myRoute ~> check {
        status should be(Created)
      }

      Get("/scenario/yoto/begin") ~> myRoute ~> check {
        responseAs[GameResponse].instructions should contain(Print("Why hello there"))
      }
    }
  }
}
