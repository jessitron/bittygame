package com.jessitron.bittygame.crux

import org.scalacheck.{Prop, Properties}
import com.jessitron.bittygame.gen._
import Prop.BooleanOperators

object OpportunityProperties extends Properties("The Opportunity concept") with GameStateGen {

  property("If it requires an object, it is not available until that object is in state") =
    Prop.forAll(alwaysAvailableOpportunity, itemGen, independentGameStateGen) {
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

  property("An item can be an obstacle that prevents an option from being taken successfully, but not from being seen") =
    Prop.forAll(itemGen, messageGen, printActionGen, scenarioAndStateGen) {
      (obstructingItem: Item,
       disappointment: MessageToThePlayer,
       someOpportunity: Opportunity,
       sas: (Scenario, GameState)) =>

        val (s, stateWithoutItem) = sas
        val trigger = someOpportunity.trigger
        val blockedOpportunity =
         someOpportunity.behindObstacle(Has(obstructingItem), disappointment)

        val scenario = s.addPossibility(blockedOpportunity)

        val stateWithItem = stateWithoutItem.addToInventory(obstructingItem)

        val (_, obstructedHappenings) = Turn.act(scenario)(stateWithItem, trigger)
        val (_, unobstructedHappenings) = Turn.act(scenario)(stateWithoutItem, trigger)

        ((obstructedHappenings.results.toSet == Set(CantDoThat(disappointment))) :| "Denied!!") &&
          ((unobstructedHappenings == someOpportunity.results) :| "Without obstacle, OK")

    }


}
