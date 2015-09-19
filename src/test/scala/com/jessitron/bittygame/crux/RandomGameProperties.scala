package com.jessitron.bittygame.crux

import com.jessitron.bittygame.scenarios.RandomScenario
import org.scalacheck.{Gen, Properties, Prop}
import org.scalacheck.Prop._

object RandomGameProperties extends Properties("Valid games") {

  val randomGameGen = for {
    optionCount <- Gen.choose(0,10)
    scenario <- RandomScenario.defaultGameGen.funGameGen(optionCount)
  } yield scenario

  property("a victory condition exists") =
    Prop.forAll(randomGameGen :| "game def") {
      scenario: Scenario =>
      scenario.opportunities.exists(_.results.results.contains(Win))
    }

  property("It never suggests the empty string at first") =
    Prop.forAll(randomGameGen) { scenario =>
      val (gameState, _) = Turn.firstTurn(scenario)
      TurnProperties.neverThinkOfBlank(scenario, gameState)
    }


}
