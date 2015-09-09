package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux.{Print, GameDefinition}
import com.jessitron.bittygame.web.messages.GameResponse
import org.scalatest.ShouldMatchers
import spray.http._
import spray.httpx.SprayJsonSupport._
import com.jessitron.bittygame.serialization._
import StatusCodes._

class BittyGameServiceSpec extends org.scalatest.FunSpec
                       with ShouldMatchers
                       with BittyGameServiceTestiness {

  describe ("the first turn") {

    it("prints the welcome message") {
      val someGame = GameDefinition(Seq(), "Why hello there")
      import spray.json.DefaultJsonProtocol._
      Put("/game/yolo", someGame) ~> myRoute ~> check {
        status should be(Created)
      }

      Get("/game/yolo/begin") ~> myRoute ~> check {
        responseAs[GameResponse].instructions should contain(Print("Why hello there"))
      }
    }
  }
}
