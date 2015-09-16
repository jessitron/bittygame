package com.jessitron.bittygame.crux

import org.scalacheck.{Prop, Properties}
import com.jessitron.bittygame.gen._
import org.scalacheck.Prop.BooleanOperators

object ActionInventoryProperties extends Properties("Actions that provide inventory") {

  property("If I take an opportunity that provides an item, then I have the item") =
    Prop.forAll {
      (scenarioAndState: (Scenario, GameState),
        someAction: Opportunity,
        item: Item) =>

      val (scenarioWithoutOpportunity, gameState) = scenarioAndState

      val actionProvidingItem = someAction.andProvides(item)
      val scenario = scenarioWithoutOpportunity.addPossibility(actionProvidingItem)

      val (nextState, _) = Turn.act(scenario)(gameState, someAction.trigger)

      nextState.hasItem(item) :| "Item not in possession."
    }

  property("If an opportunity requires an item, it is not available until we have the item") =
    Prop.forAll {
      (scenarioAndState: (Scenario, GameState),
       someAction: Opportunity,
       item: Item) =>

        val (scenarioWithoutOpportunity, gameState) = scenarioAndState

        (!gameState.hasItem(item)) ==> {

          val actionRequiringItem = someAction.onlyIf(Has(item))
          val scenario = scenarioWithoutOpportunity.addPossibility(actionRequiringItem)

          val (_, happenings) = Turn.act(scenario)(gameState, someAction.trigger)

          happenings.results.contains(IDontKnowHowTo(someAction.trigger)) :|
            s"Should not have been able to do that. Result: $happenings\nAction: $actionRequiringItem"
        }
    }




}
