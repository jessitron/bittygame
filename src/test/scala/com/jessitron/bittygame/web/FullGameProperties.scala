package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux.{ScenarioTitle, Print, Scenario}
import com.jessitron.bittygame.gen.{ScenarioTitleGen, GameStateGen}
import com.jessitron.bittygame.web.identifiers.GameID
import com.jessitron.bittygame.web.messages.GameResponse
import com.jessitron.bittygame.web.ports.ScenarioDAO
import org.scalacheck.{Shrink, Gen}
import spray.httpx.SprayJsonSupport._
import com.jessitron.bittygame.serialization._
import spray.json.DefaultJsonProtocol._
import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalacheck.Prop.BooleanOperators

trait FullGameGen extends GameStateGen with ScenarioTitleGen {

  val scenarioDAO : ScenarioDAO

  val SCENARIOS_TO_GENERATE = 10
  val severalScenarios : Seq[Scenario] =
    Iterator.continually(scenarioGen.sample).collect{ case Some(a) => a}.take(SCENARIOS_TO_GENERATE).toSeq

  /*** STORE THEM ***/
  severalScenarios.foreach { case scenario => scenarioDAO.save(scenario.title, scenario)}

  val storedScenario: Gen[Scenario] = Gen.oneOf(severalScenarios)

}

class FullGameProperties
  extends org.scalatest.PropSpec
  with GeneratorDrivenPropertyChecks
  with ShouldMatchers
  with BittyGameServiceTestiness
  with FullGameGen {

  val whatINeed: Gen[(ScenarioTitle, Seq[String], Seq[String])] = for {
    scenario <- storedScenario
    validMoves = scenario.possibilities.map(_.trigger)
    someValidMoves <- Gen.listOf(Gen.oneOf(validMoves))
    someInvalidMoves <- Gen.listOf(triggerGen.suchThat(!someValidMoves.contains(_)))
  } yield (scenario.title, someValidMoves, someInvalidMoves)

  // The magic: prevent ScalaTest from shrinking the key, which is a string. This also prevents shrinking the other things. Oh well.
  implicit val dontShrinkThisDammit: Shrink[(ScenarioTitle, Seq[String], Seq[String])] = Shrink(t => Stream())

  property("Anything returned by Think, it knows how to do") {
    forAll(whatINeed) { input =>
      val (key, someValidMoves, someInvalidMoves) = input
      // sometimes shrinking is your enemy
    //  (key.nonEmpty) ==> {

        val moves = scala.util.Random.shuffle(someValidMoves ++ someInvalidMoves)

        val gameID = callToTheFirstGameEndpoint(key) ~> myRoute ~> check {
          responseAs[GameResponse].gameID
        }

        val think = callToTheThinkEndpoint(gameID) ~> myRoute ~> check {
          responseAs[Seq[String]]
        }

        def takeMove(gameID: GameID, move: String) =
          Get("/game/" + gameID + "/" + move) ~> myRoute ~> check {
            responseAs[GameResponse]
          }

      // Next: ok actually check that each of those are available
      // and that everythign else in our list is not.


      // and then take some moves and confirm that this is still true at every step

        true

   //   }


    }

  }

}
