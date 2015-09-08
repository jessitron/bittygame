package com.jessitron.bittygame.crux

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Assertions, PropSpec}
import com.jessitron.bittygame.gen._

class StartGameTest extends PropSpec with GeneratorDrivenPropertyChecks with Assertions {

   property("The game starts with an empty inventory") {
     forAll { (gameDef: GameDefinition) =>
       val (initialState, happenings) = Turn.firstTurn(gameDef)
       assert(initialState.inventory.isEmpty, "inventory should be empty on first turn")
     }
   }

  property("The game starts with some message") {
    forAll { (gameDef: GameDefinition) =>
      val (initialState, happenings) = Turn.firstTurn(gameDef)

      val printedMessages = happenings.results.collect {
        case Print(msg) => msg
      }

      assert(printedMessages.nonEmpty, "every game should print something")
    }
  }

  property("If there are no available options, the game exits") {
    // right now all options are available, so this is the same as "there are no options"
    forAll { gameDef : GameDefinition =>
      val gameWithoutOptions = gameDef.copy(possibilities = Seq())
      val (_, happenings) = Turn.firstTurn(gameDef)
      assert(happenings.results.contains(ExitGame))
    }
  }

  property("Iff there are no available options, exit on first turn") {
    forAll { gameDef : GameDefinition =>
      forAll(gameStateGen(gameDef)) { gameState : GameState =>
        val (_, happenings) = Turn.firstTurn(gameDef)
        val anythingAvailable: Boolean = gameDef.possibilities.exists(_.available(gameState))
        val autoExit: Boolean = happenings.results.contains(ExitGame)
        assert(autoExit === !anythingAvailable,
          s"Should only exit if no options are available. Exiting? $autoExit Available? $anythingAvailable")
      }
    }
  }

}
