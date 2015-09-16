package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux.{Print, Scenario}
import com.jessitron.bittygame.gen.{GameNameGen, GameStateGen}
import com.jessitron.bittygame.web.identifiers.ScenarioKey
import com.jessitron.bittygame.web.messages.GameResponse
import com.jessitron.bittygame.web.ports.ScenarioDAO
import org.scalacheck.Gen
import spray.httpx.SprayJsonSupport._
import com.jessitron.bittygame.serialization._
import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks

trait FullGameGen extends GameStateGen with GameNameGen {

  val scenarioDAO : ScenarioDAO

  val scenarioAndName = for {
    scenarioName <- gameNameGen
    scenario <- scenarioGen
  } yield (scenarioName, scenario)

  val SCENARIOS_TO_GENERATE = 10
  val severalScenarios : Seq[(ScenarioKey, Scenario)] =
    Iterator.continually(scenarioAndName.sample).collect{ case Some(a) => a}.take(SCENARIOS_TO_GENERATE).toSeq

  /*** STORE THEM ***/
  severalScenarios.foreach { case (key, scenario) => scenarioDAO.save(key, scenario)}

  val storedScenario: Gen[(ScenarioKey, Scenario)] = Gen.oneOf(severalScenarios)

  val whatINeed = for {
    (key, scenario) <- storedScenario
    validMoves = scenario.possibilities.map(_.trigger)
    someValidMoves <- Gen.listOf(Gen.oneOf(validMoves))
    someInvalidMoves <- Gen.listOf(triggerGen.suchThat(!someValidMoves.contains(_)))
  } yield (key, someValidMoves, someInvalidMoves)

}

class FullGameProperties
  extends org.scalatest.PropSpec
  with GeneratorDrivenPropertyChecks
  with ShouldMatchers
  with BittyGameServiceTestiness
  with FullGameGen {

  property("Anything returned by Think, it knows how to do") {
    forAll(whatINeed) { input =>
      val (key, someValidMoves, someInvalidMoves) = input

      val moves = scala.util.Random.shuffle(someValidMoves ++ someInvalidMoves)

      val gameId = Get("/game/" + key + "/begin") ~> myRoute ~> check {
        responseAs[GameResponse].gameID
      }

      // TODO: complete

      true


    }

  }

}
