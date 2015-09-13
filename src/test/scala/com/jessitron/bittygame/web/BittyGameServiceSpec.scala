package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux.{Print, GameDefinition}
import com.jessitron.bittygame.web.messages.GameResponse
import org.scalatest.ShouldMatchers
import spray.http._
import spray.httpx.SprayJsonSupport._
import com.jessitron.bittygame.serialization._
import StatusCodes._

class BittyGameServiceSpec extends org.scalatest.PropSpec
                       with ShouldMatchers
                       with BittyGameServiceTestiness {

  property ("the first turn prints the welcome message") {
    val someGame = GameDefinition(Seq(), "Why hello there")
    Put("/game/yolo", someGame) ~> myRoute ~> check {
      status should be(Created)
    }

    Get("/game/yolo/begin") ~> myRoute ~> check {
      responseAs[GameResponse].instructions should contain(Print("Why hello there"))
    }
  }
}
