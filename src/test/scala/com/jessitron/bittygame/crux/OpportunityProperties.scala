package com.jessitron.bittygame.crux

import org.scalacheck.{Prop, Properties}
import com.jessitron.bittygame.gen._
import Prop.BooleanOperators

object OpportunityProperties extends Properties("The Opportunity concept") with GameStateGen {

  property("If it requires an object, it is not available until that object is in state") =
    Prop.forAll(alwaysAvailableOpportunity, itemGen, independentGameStateGen) {
      (opportunity: Opportunity,
       item: Item,
       gameState: GameState) =>

        (!gameState.hasItem(item)) ==> {
          val opportunityRequiringItem = opportunity.onlyIf(Has(item))
          val gameStateWithItem = gameState.addToInventory(item)

          (!opportunityRequiringItem.available(gameState) :| "Not available without item") &&
            (opportunityRequiringItem.available(gameStateWithItem) :| "Available with item")
        }
    }



}
