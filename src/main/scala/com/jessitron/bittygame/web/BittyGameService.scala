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

class BittyGameServiceActor extends Actor with BittyGameService {

  def actorRefFactory = context
  val executionContext = context.dispatcher

  def receive = runRoute(myRoute)

  val gameDefinitions = new TrivialGameDefinitionDAO() // for realz, I'd put this in an actor

  gameDefinitions.save("hungover", com.jessitron.bittygame.games.Hungover.gameDef)
}

trait BittyGameService extends HttpService {

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