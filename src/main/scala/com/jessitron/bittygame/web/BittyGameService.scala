package com.jessitron.bittygame.web

import akka.actor.Actor
import com.jessitron.bittygame.crux.{Turn,GameDefinition}
import com.jessitron.bittygame.games.RandomGame
import com.jessitron.bittygame.web.messages.{CreateRandomGameResponse, GameResponse}
import com.jessitron.bittygame.web.ports.{TrivialGameDefinitionDAO, GameDefinitionDAO}
import spray.routing._
import spray.http._
import spray.json.DefaultJsonProtocol._
import spray.httpx.SprayJsonSupport._
import com.jessitron.bittygame.serialization._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

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
      def theFutureIsGreat = gameDefinitions.retrieve(seg).map { gameDef =>
        GameResponse(Turn.firstTurn(gameDef))
      }
      onComplete(theFutureIsGreat) {
          case Success(yay) => complete(yay)
          case Failure(t: GameDefinitionDAO.NotFoundException) => complete(StatusCodes.NotFound)
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

  private val createRandomGame: Route = path ("random") {
    (get | put) {
      complete {
        val newName = RandomGame.name(gameDefinitions.names())
        gameDefinitions.save(newName, RandomGame.create()).map(
          _ => CreateRandomGameResponse(newName, s"/game/${java.net.URLEncoder.encode(newName, "UTF-8")}/begin")
        ).map( x => StatusCodes.Created -> x)
      }
    }
  }

  val myRoute =
    firstTurn ~ createGameDef ~ createRandomGame
}