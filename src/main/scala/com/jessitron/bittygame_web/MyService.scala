package com.jessitron.bittygame_web

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import spray.json._
import spray.json.DefaultJsonProtocol._
import spray.httpx.SprayJsonSupport._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}


// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  val myRoute =
    path("game" / Segment / "begin" ) { seg =>
      get {
        respondWithMediaType(`application/json`) {
          complete {
            Map("foo" -> "bar", "Say hello" -> "baz")
          }
        }
      }
    }
}