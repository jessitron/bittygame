package com.jessitron.bittygame.gen

import com.jessitron.bittygame.crux.{GameState, Scenario}
import org.scalacheck.util.Pretty
import org.scalacheck.{Arbitrary, Gen}

trait GameStateGen extends ScenarioGen with ItemGen {

  def gameStateGen(scenario: Scenario): Gen[GameState] = GameState.init // TODO: use the items in the game

  val independentGameStateGen : Gen[GameState] =
    for {
      itemCount <- Gen.choose(0,4)
      items <- Gen.listOfN(itemCount, itemGen)
    } yield GameState(items)

  def scenarioAndStateGen: Gen[(Scenario, GameState)] =
    for {
      scenario <- scenarioGen
      gameState <- gameStateGen(scenario)
    } yield (scenario, gameState)

  implicit val arbScenarioAndState: Arbitrary[(Scenario, GameState)] = Arbitrary(scenarioAndStateGen)

  implicit def prettyGameAndState(gameAndState: (Scenario, GameState)): Pretty =
    Pretty { p =>
      val (scenario, gameState) = gameAndState
      val scenarioPretty = implicitly[Scenario => Pretty]
       s"GameState: $gameState\n" + scenarioPretty(scenario)(p)
    }
}
