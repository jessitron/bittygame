package com.jessitron.bittygame.crux

import com.jessitron.bittygame.gen.ScenarioGen
import org.scalacheck.{Properties, Prop}
import org.scalacheck.Prop.BooleanOperators

object ScenarioProperties extends Properties("What is this even") with ScenarioGen {

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
