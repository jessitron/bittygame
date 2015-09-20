package com.jessitron.bittygame.web

import akka.actor.Actor
import com.jessitron.bittygame.crux._
import com.jessitron.bittygame.scenarios.RandomScenario
import com.jessitron.bittygame.web.messages.{CreateRandomScenarioResponse, GameResponse}
import com.jessitron.bittygame.web.ports.GameStateDAO.SaveResult
import com.jessitron.bittygame.web.ports.{ScenarioDAO, TrivialGameStateDAO, GameStateDAO, TrivialScenarioDAO}
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

  val scenarioDAO = new TrivialScenarioDAO() // for realz, I'd put this in an actor
  val gameStates = new TrivialGameStateDAO() // for realz, I'd put this in an actor

  scenarioDAO.save(com.jessitron.bittygame.scenarios.Hungover.scenario)
  scenarioDAO.save(com.jessitron.bittygame.scenarios.JessLife.scenario)
}

trait BittyGameService extends HttpService {

  private val allowOriginHeader = `Access-Control-Allow-Origin`(AllOrigins)
  private val allowOrdinaryHeaders = `Access-Control-Allow-Headers`("Content-Type")

  implicit val executionContext: ExecutionContext
  val scenarioDAO: ScenarioDAO
  val gameStates: GameStateDAO

  private val firstTurn: Route = path("scenario" / Segment / "begin") { seg =>
    val gameName = java.net.URLDecoder.decode(seg, "UTF-8")
    get { // refactor into for comprehension?
      def theFutureIsGreat = scenarioDAO.retrieve(gameName).map { scenario =>
        val (gameState, whatHappens) = Turn.firstTurn(scenario)
        gameStates.store(gameState).map { case SaveResult(gameID) =>
          GameResponse(gameID, whatHappens.tellTheClient)
        }
      }
      handleNotFound("boo hoo")(theFutureIsGreat)
    }
  }

  private val act : Route = path("game" / Segment / "turn" / Segment ) {
    (seg1, seg2) =>
      val gameID = java.net.URLDecoder.decode(seg1, "UTF-8")
      val move   = java.net.URLDecoder.decode(seg2, "UTF-8")
      get {
          val act = for {
            gameState <- gameStates.recall(gameID)
            scenario <- scenarioDAO.retrieve(gameState.title)
            (newState, happenings) = Turn.act(scenario)(gameState, move)
            save <- gameStates.update(gameID, newState)
          } yield GameResponse(save.gameID, happenings.tellTheClient)

          handleNotFound("turn poo")(act)
      } ~
      options {
        // NO REALLY IT'S OK
        respondWithHeaders(allowOrdinaryHeaders) { complete(StatusCodes.OK) }
      }
  }

  private val think: Route = path("game" / Segment / "think") { seg =>
    val gameID = java.net.URLDecoder.decode(seg, "UTF-8")
    get {
      val stuff = for{
        state <- gameStates.recall(gameID)
        scenario <- scenarioDAO.retrieve(state.title)
      } yield Turn.think(scenario, state)
      handleNotFound("darn it")(stuff)
    } ~
    options {
      // NO REALLY IT'S OK
      respondWithHeaders(allowOrdinaryHeaders) { complete(StatusCodes.OK) }
    }
  }

  private val createScenario: Route = path("scenario" /) {
    entity(as[Scenario]) { scenario =>
      put {
        complete(scenarioDAO.save(scenario).map(_ => StatusCodes.Created))
      }
    }
  }

  private val createRandomGame: Route = path ("random") {
    (get | put) {
      complete {
        val newScenario = RandomScenario.create()
        scenarioDAO.save(newScenario).map(
          _ => CreateRandomScenarioResponse(newScenario.title, firstTurnUrl(newScenario.title))
        ).map( x => StatusCodes.Created -> x)
      }
    }
  }

  def firstTurnUrl(name: ScenarioTitle): String = {
    s"/scenario/${java.net.URLEncoder.encode(name, "UTF-8")}/begin"
  }

  val myRoute =
    respondWithHeaders(allowOriginHeader) { firstTurn ~ act ~ createScenario ~ createRandomGame ~ think }


  private def handleNotFound[X](complaint: String)(x: Future[X])(implicit ev1: X => ToResponseMarshallable) =
    onComplete(x) {
      case Success(yay) => complete(yay)
      case Failure(t: ScenarioDAO.NotFoundException) =>
        complete(StatusCodes.NotFound, complaint + " the scenario: " + t.getMessage)
      case Failure(t: GameStateDAO.NotFoundException) =>
        complete(StatusCodes.NotFound, complaint + " the state: " + t.getMessage)
      case Failure(other) => throw other // avoid compiler warning
    }
}