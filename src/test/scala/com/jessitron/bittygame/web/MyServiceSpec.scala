package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux.GameDefinition
import org.scalatest.ShouldMatchers
import spray.testkit.ScalatestRouteTest
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.json._
import StatusCodes._

class MyServiceSpec extends org.scalatest.FunSpec
                       with ScalatestRouteTest
                       with ShouldMatchers
                       with MyService {
  def actorRefFactory = system
  
  describe ("MyService") {

    it("return a greeting for GET requests to the root path") {
      Get("/game/yolo/begin") ~> myRoute ~> check {
        responseAs[String].contains("Say hello") should be(true)
      }
    }

    it("prints the welcome message") {
      val someGame = GameDefinition(Seq(), "Why hello there")
      import spray.json.DefaultJsonProtocol._
      Put("/game/yolo", someGame) ~> myRoute ~> check {
        status === Created
      }
    }

  }
}
