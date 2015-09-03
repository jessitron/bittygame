package com.jessitron.bittygame_web

import org.scalatest.ShouldMatchers
import spray.testkit.ScalatestRouteTest
import spray.http._
import StatusCodes._

class MyServiceSpec extends org.scalatest.FunSpec
                       with ScalatestRouteTest
                       with ShouldMatchers
                       with MyService {
  def actorRefFactory = system
  
  describe ("MyService") {

    it("return a greeting for GET requests to the root path") {
      Get("game/yolo/begin") ~> myRoute ~> check {
        responseAs[String] should contain("Say hello")
      }
    }

    it("leave GET requests to other paths unhandled") {
      Get("/kermit") ~> myRoute ~> check {
        handled should be(false)
      }
    }

    it("return a MethodNotAllowed error for PUT requests to the root path") {
      Put() ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }
  }
}
