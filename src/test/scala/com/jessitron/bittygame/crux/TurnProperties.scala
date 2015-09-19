package com.jessitron.bittygame.crux

import com.jessitron.bittygame.gen._
import org.scalacheck.{Properties, Prop}
import org.scalacheck.Prop.BooleanOperators
import org.scalatest.{Assertions, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

object TurnProperties extends Properties("Taking a turn") with GameStateGen {

  property("Victory actions result in exit") =
    Prop.forAll(scenarioAndStateGen, triggerGen, messageGen) {
      (gameAndState, trigger, message) =>
        val (someScenario, gameState) = gameAndState

        val win = Opportunity.victory(trigger, message)

        (!someScenario.opportunities.exists(_.conflictsWith(win))) ==> {

          val scenario = someScenario.addPossibility(win)

          val (newState, happenings) = Turn.act(scenario)(gameState, trigger)

          happenings.results.contains(ExitGame) :| s"the results were ${happenings.results}"
        }
    }

  val neverThinkOfBlank: ((Scenario, GameState)) => Prop = {
    case (scenario, gameState) =>
      !Turn.think(scenario, gameState).contains("")
  }

  property("Never returns a blank option") =
    Prop.forAll(scenarioAndStateGen)(neverThinkOfBlank)

  // TODO: all turns result either in IDontKnowHowTo OR any number of other things

  // TODO: if IDontKnowHowTo, then state is unchanged

}

object FirstTurnProperties
  extends PropSpec
  with GeneratorDrivenPropertyChecks
  with Assertions
  with GameStateGen {
  {
    property("The game starts with an empty inventory") {
      forAll { (scenario: Scenario) =>
        val (initialState, happenings) = Turn.firstTurn(scenario)
        assert(initialState.inventory.isEmpty, "inventory should be empty on first turn")
      }
    }

    property("The game starts with some message") {
      forAll { (scenario: Scenario) =>
        val (initialState, happenings) = Turn.firstTurn(scenario)

        val printedMessages = happenings.results.collect {
          case Print(msg) => msg
        }
        assert(printedMessages.nonEmpty, "every game should print something")
      }
    }

    property("Iff there are no available options, exit on first turn") {
      forAll { scenario: Scenario =>
        forAll(gameStateGen(scenario)) { gameState: GameState =>
          val (_, happenings) = Turn.firstTurn(scenario)
          val anythingAvailable: Boolean = scenario.opportunities.exists(_.available(gameState))
          val autoExit: Boolean = happenings.results.contains(ExitGame)
          assert(autoExit == !anythingAvailable,
            s"Should only exit if no options are available. Exiting? $autoExit Available? $anythingAvailable")
        }
      }
    }
  }
}

