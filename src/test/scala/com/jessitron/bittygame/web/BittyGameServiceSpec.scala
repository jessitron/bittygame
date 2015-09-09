package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux.{Print, GameDefinition}
import com.jessitron.bittygame.web.ports.TrivialGameDefinitionDAO
import org.scalatest.ShouldMatchers
import spray.testkit.ScalatestRouteTest
import spray.http._
import spray.httpx.SprayJsonSupport._
import com.jessitron.bittygame.serialization._
import StatusCodes._

class BittyGameServiceSpec extends org.scalatest.FunSpec
                       with ScalatestRouteTest
                       with ShouldMatchers
                       with BittyGameService {
  def actorRefFactory = system
  val gameDefinitions = new TrivialGameDefinitionDAO()
  val executionContext = scala.concurrent.ExecutionContext.global
  
  describe ("MyService") {

    it("prints the welcome message") {
      val someGame = GameDefinition(Seq(), "Why hello there")
      import spray.json.DefaultJsonProtocol._
      Put("/game/yolo", someGame) ~> myRoute ~> check {
        status === Created
      }

      Get("/game/yolo/begin") ~> myRoute ~> check {
        responseAs[GameResponse].instructions should contain(Print("Why hello there"))
      }
    }

  }
}