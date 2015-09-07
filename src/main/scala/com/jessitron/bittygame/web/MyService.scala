package com.jessitron.bittygame.web

import akka.actor.Actor
import com.jessitron.bittygame.crux.{Turn,GameDefinition}
import com.jessitron.bittygame.web.ports.{TrivialGameDefinitionDAO, GameDefinitionDAO}
import spray.routing._
import spray.http._
import spray.json.DefaultJsonProtocol._
import spray.httpx.SprayJsonSupport._
import com.jessitron.bittygame.serialization._

import scala.concurrent.ExecutionContext

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context
  val executionContext = context.dispatcher

  def receive = runRoute(myRoute)

  val gameDefinitions = new TrivialGameDefinitionDAO() // for realz, I'd put this in an actor
}

trait MyService extends HttpService {

  implicit val executionContext: ExecutionContext
  val gameDefinitions: GameDefinitionDAO

  private val firstTurn: Route = path("game" / Segment / "begin") { seg =>
    get {
      complete {
        gameDefinitions.retrieve(seg).map { gameDef =>
          GameResponse(Turn.firstTurn(gameDef))
        }
      }
    }
  }

  private val createGameDef: Route = path("game" / Segment) { seg =>
    entity(as[GameDefinition]) { gameDef =>
      put {
        complete(gameDefinitions.save(seg, gameDef).map(_ => StatusCodes.Created))
      }
    }
  }
  val myRoute =
    firstTurn ~ createGameDef
}