package com.jessitron.bittygame.crux

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Assertions, PropSpec}
import com.jessitron.bittygame.gen._

class StartGameTest extends PropSpec with GeneratorDrivenPropertyChecks with Assertions with GameStateGen {

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
    forAll { scenario : Scenario =>
      forAll(gameStateGen(scenario)) { gameState : GameState =>
        val (_, happenings) = Turn.firstTurn(scenario)
        val anythingAvailable: Boolean = scenario.possibilities.exists(_.available(gameState))
        val autoExit: Boolean = happenings.results.contains(ExitGame)
        assert(autoExit == !anythingAvailable,
          s"Should only exit if no options are available. Exiting? $autoExit Available? $anythingAvailable")
      }
    }
  }

}
