package com.jessitron.bittygame.web

import akka.actor.Actor
import com.jessitron.bittygame.crux.{GameState, Turn, GameDefinition}
import com.jessitron.bittygame.games.RandomGame
import com.jessitron.bittygame.web.messages.{GameTurn, CreateRandomGameResponse, GameResponse}
import com.jessitron.bittygame.web.ports.GameDefinitionDAO.GameDefinitionKey
import com.jessitron.bittygame.web.ports.{TrivialGameDefinitionDAO, GameDefinitionDAO}
import spray.http.HttpHeaders.{`Access-Control-Allow-Headers`, `Access-Control-Allow-Origin`}
import spray.httpx.marshalling.ToResponseMarshallable
import spray.routing._
import spray.http._
import spray.json.DefaultJsonProtocol._
import spray.httpx.SprayJsonSupport._
import com.jessitron.bittygame.serialization._

import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Failure, Success}

class BittyGameServiceActor extends Actor with BittyGameService {

  def actorRefFactory = context
  val executionContext = context.dispatcher

  def receive = runRoute(myRoute)

  val gameDefinitions = new TrivialGameDefinitionDAO() // for realz, I'd put this in an actor

  gameDefinitions.save("hungover", com.jessitron.bittygame.games.Hungover.gameDef)
}

trait BittyGameService extends HttpService {

  private val allowOriginHeader = `Access-Control-Allow-Origin`(AllOrigins)
  private val allowOrdinaryHeaders = `Access-Control-Allow-Headers`("Content-Type")

  implicit val executionContext: ExecutionContext
  val gameDefinitions: GameDefinitionDAO

  private val firstTurn: Route = path("game" / Segment / "begin") { seg =>
    val gameName = java.net.URLDecoder.decode(seg, "UTF-8")
    get {
      def theFutureIsGreat = gameDefinitions.retrieve(gameName).map { gameDef =>
        GameResponse(Turn.firstTurn(gameDef))
      }
      handleNotFound("boo hoo")(theFutureIsGreat)
    }
  }

  private val act : Route = path("game" / Segment / "turn") {
    gameName =>
      post {
        entity(as[GameTurn]) { turn =>
          val fo = gameDefinitions.retrieve(gameName) map { gameDef =>
            Turn.act(gameDef)(turn.state, turn.typed)
          }
          handleNotFound("turn poo")(fo)
        }
      }
  }

  private def handleNotFound[X <% ToResponseMarshallable](complaint: String)(x: Future[X]) =
    onComplete(x) {
      case Success(yay) => complete(yay)
      case Failure(t: GameDefinitionDAO.NotFoundException) =>
        complete(StatusCodes.NotFound, complaint)
      case Failure(other) => throw other // avoid compiler warning
    }

  private val think: Route = path("game" / Segment / "think") { seg =>
    post {
      entity(as[GameState]) { state =>
        def stuff = gameDefinitions.retrieve(seg).map { gameDef =>
          gameDef.possibilities.map {
            _.trigger
          }
        }
       handleNotFound("darn it")(stuff)
      }
    } ~
    options {
      // NO REALLY IT'S OK TO POST
      respondWithHeaders(allowOrdinaryHeaders) { complete(StatusCodes.OK) }
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
    respondWithHeaders(allowOriginHeader) { firstTurn ~ act ~ createGameDef ~ createRandomGame ~ think }
}