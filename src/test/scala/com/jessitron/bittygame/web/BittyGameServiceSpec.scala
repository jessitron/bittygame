package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux.{PlayerAction, Print, GameDefinition}
import com.jessitron.bittygame.web.messages.GameResponse
import org.scalatest.ShouldMatchers
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import spray.http._
import spray.httpx.SprayJsonSupport._
import com.jessitron.bittygame.serialization._
import StatusCodes._
import com.jessitron.bittygame.gen._
import org.scalacheck.Gen._

class BittyGameServiceSpec extends org.scalatest.PropSpec
                       with GeneratorDrivenPropertyChecks
                       with ShouldMatchers
                       with BittyGameServiceTestiness {

  property ("the first turn prints the welcome message") {
    forAll { (someActions: Seq[PlayerAction],
              message: String) =>
      val someGame = GameDefinition(Seq(), message)
      Put("/game/yolo", someGame) ~> myRoute ~> check {
        status should be(Created)
      }

      Get("/game/yolo/begin") ~> myRoute ~> check {
        responseAs[GameResponse].instructions should contain(Print(message))
      }
    }
  }
}
