package com.jessitron.bittygame.web

import akka.actor.Actor
import com.jessitron.bittygame.crux.{GameState, Turn, GameDefinition}
import com.jessitron.bittygame.games.RandomGame
import com.jessitron.bittygame.web.messages.{CreateRandomGameResponse, GameResponse}
import com.jessitron.bittygame.web.ports.GameDefinitionDAO.GameDefinitionKey
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
    val gameName = java.net.URLDecoder.decode(seg, "UTF-8")
    get {
      def theFutureIsGreat = gameDefinitions.retrieve(gameName).map { gameDef =>
        GameResponse(Turn.firstTurn(gameDef))
      }
      onComplete(theFutureIsGreat)  {
        case Success(yay) => complete(yay)
        case Failure(t: GameDefinitionDAO.NotFoundException) => complete(StatusCodes.NotFound)
      }
    }
  }

  private val think: Route = path("game" / Segment / "think") { seg =>
    post {
      entity(as[GameState]) { state =>
        def stuff = gameDefinitions.retrieve(seg).map { gameDef =>
          gameDef.possibilities.map {
            _.trigger
          }
        }
        onComplete(stuff) {
          case Success(yay) => complete(yay)
          case Failure(t: GameDefinitionDAO.NotFoundException) => complete(StatusCodes.NotFound, "thanks for trying")
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

  private val createRandomGame: Route = path ("random") {
    (get | put) {
      complete {
        val newName = RandomGame.name(gameDefinitions.names())
        gameDefinitions.save(newName, RandomGame.create()).map(
          _ => CreateRandomGameResponse(newName, firstTurnUrl(newName))
        ).map( x => StatusCodes.Created -> x)
      }
    }
  }

  def firstTurnUrl(name: GameDefinitionKey): String = {
    s"/game/${java.net.URLEncoder.encode(name, "UTF-8")}/begin"
  }

  val myRoute =
    firstTurn ~ createGameDef ~ createRandomGame ~ think
}