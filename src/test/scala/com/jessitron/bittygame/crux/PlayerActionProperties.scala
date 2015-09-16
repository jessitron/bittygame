package com.jessitron.bittygame.crux

import org.scalacheck.{Prop, Properties}
import com.jessitron.bittygame.gen._
import Prop.BooleanOperators

class PlayerActionProperties extends Properties("The PlayerAction class") {

  property("If it requires an object, it is not available until that object is in state") =
    Prop.forAll(playerActionGen, itemGen, independentGameStateGen) {
      (playerAction: Opportunity,
       item: Item,
       gameState: GameState) =>

        (!gameState.hasItem(item)) ==> {
          val actionRequiringItem = playerAction.onlyIf(Has(item))
          val gameStateWithItem = gameState.addToInventory(item)

          (!actionRequiringItem.available(gameState) :| "Not available without item") &&
            (actionRequiringItem.available(gameStateWithItem) :| "Available with item")
        }
    }

}
