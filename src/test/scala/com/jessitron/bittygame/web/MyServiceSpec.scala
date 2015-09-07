package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux.{Print, GameDefinition}
import org.scalatest.ShouldMatchers
import spray.testkit.ScalatestRouteTest
import spray.http._
import spray.httpx.SprayJsonSupport._
import com.jessitron.bittygame.serialization._
import StatusCodes._

class MyServiceSpec extends org.scalatest.FunSpec
                       with ScalatestRouteTest
                       with ShouldMatchers
                       with MyService {
  def actorRefFactory = system
  
  describe ("MyService") {

    it("prints the welcome message") {
      val someGame = GameDefinition(Seq(), "Why hello there")
      import spray.json.DefaultJsonProtocol._
      Put("/game/yolo", someGame) ~> myRoute ~> check {
        status === Created
      }

      Get("/game/yolo/begin") ~> myRoute ~> check {
        responseAs[GameResponse].instructions.results should contain(Print("Why hello there"))
      }
    }

  }
}
