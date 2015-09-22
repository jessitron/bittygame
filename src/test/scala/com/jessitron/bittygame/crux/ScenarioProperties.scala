package com.jessitron.bittygame.crux

import com.jessitron.bittygame.gen.ScenarioGen
import org.scalacheck.{Properties, Prop}
import org.scalacheck.Prop.BooleanOperators

object ScenarioProperties extends Properties("What is this even") with ScenarioGen {

  val whatINeed = for {
    scenario <- scenarioGen
    items = scenario.items
    if items.nonEmpty
    opp <- opportunityGen(items, scenario.stats)
    if noConflict(scenario, opp)
    if opp.obstacles.isEmpty // no obstacles currently, so we'll be sure to be blocked by the one we create
    state <- gameStateGen(scenario.title, items, scenario.stats)
    if opp.available(state)
    message <- messageGen
  } yield (items.head, message, opp, scenario, state)

  property("An item can be an obstacle that prevents an option from being taken successfully, but not from being seen") =
    Prop.forAll(whatINeed) {
      case (obstructingItem: Item,
      disappointment: MessageToThePlayer,
      someOpportunity: Opportunity,
      s: Scenario,
      state: GameState) =>

        val trigger = someOpportunity.trigger
        val blockedOpportunity =
          someOpportunity.behindObstacle(Has(obstructingItem), disappointment)

        val scenario = s.addPossibility(blockedOpportunity)

        val (postState, happenings) = Turn.act(scenario)(state, trigger)

        if (state.hasItem(obstructingItem)) {
          ((happenings.results.toSet == Set(CantDoThat(disappointment))) :| s"Denied!! $happenings") &&
            ((postState == state) :| s"State doesn't change when obstructed. ${postState}")
        } else {
          (happenings == someOpportunity.results) :| "Without obstacle, OK"
        }

    }
}
