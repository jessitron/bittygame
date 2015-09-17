package com.jessitron.bittygame.crux

import org.scalacheck.{Prop, Properties}
import com.jessitron.bittygame.gen._
import org.scalacheck.Prop.BooleanOperators

object ActionInventoryProperties extends Properties("Actions that provide inventory") with GameStateGen {

  property("If I take an opportunity that provides an item, then I have the item") =
    Prop.forAll(scenarioAndStateGen :| "scenario and state", alwaysAvailableOpportunity :| "The Opportunity", itemGen :| "Item in question") {
      (scenarioAndState: (Scenario, GameState),
        someAction: Opportunity,
        item: Item) =>

      val (scenarioWithoutOpportunity, gameState) = scenarioAndState

      (!scenarioWithoutOpportunity.possibilities.exists(_.conflictsWith(someAction))) ==> {
        val actionProvidingItem = someAction.andProvides(item)
        val scenario = scenarioWithoutOpportunity.addPossibility(actionProvidingItem)

        val (nextState, wh) = Turn.act(scenario)(gameState, someAction.trigger)

        nextState.hasItem(item) :| s"Item not in possession. Happenings: $wh"
      }
    }

  property("If an opportunity requires an item, it is not available until we have the item") =
    Prop.forAll {
      (scenarioAndState: (Scenario, GameState),
       someAction: Opportunity,
       item: Item) =>

        val (scenarioWithoutOpportunity, gameState) = scenarioAndState

        (!scenarioWithoutOpportunity.possibilities.exists(_.conflictsWith(someAction))) ==>
          (!gameState.hasItem(item)) ==> {

            val actionRequiringItem = someAction.onlyIf(Has(item))
            val scenario = scenarioWithoutOpportunity.addPossibility(actionRequiringItem)

            val (_, happenings) = Turn.act(scenario)(gameState, someAction.trigger)

            happenings.results.contains(IDontKnowHowTo(someAction.trigger)) :|
              s"Should not have been able to do that. Result: $happenings\nAction: $actionRequiringItem"
          }
    }




}
