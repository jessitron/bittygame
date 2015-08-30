package com.jessitron.bittygame

import org.scalatest.{Assertions, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import generators._

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

}
