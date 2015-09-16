package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux.{MessageToThePlayer, Opportunity, Print, Scenario}
import com.jessitron.bittygame.web.messages.GameResponse
import org.scalatest.ShouldMatchers
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import spray.http._
import spray.httpx.SprayJsonSupport._
import com.jessitron.bittygame.serialization._
import StatusCodes._
import com.jessitron.bittygame.gen._

class BittyGameServiceSpec extends org.scalatest.PropSpec
                       with GeneratorDrivenPropertyChecks
                       with ShouldMatchers
                       with BittyGameServiceTestiness {

  property ("the first turn prints the welcome message") {
    forAll(possibilitiesGen,
           welcomeMessageGen)
    { (someActions: Seq[Opportunity],
       message: MessageToThePlayer) =>
      val someGame = Scenario(Seq(), message)
      Put("/game/yolo", someGame) ~> myRoute ~> check {
        status should be(Created)
      }

      Get("/game/yolo/begin") ~> myRoute ~> check {
        responseAs[GameResponse].instructions should contain(Print(message))
      }
    }
  }
}
