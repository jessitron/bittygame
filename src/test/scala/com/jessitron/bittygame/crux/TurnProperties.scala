package com.jessitron.bittygame.crux

import com.jessitron.bittygame.gen._
import org.scalacheck.Prop
import org.scalacheck.Prop.BooleanOperators

object TurnProperties extends org.scalacheck.Properties("Taking a turn") with GameStateGen {

  property("Victory actions result in exit") =
    Prop.forAll(scenarioAndStateGen, triggerGen, messageGen ) { (gameAndState, trigger, message) =>
      val (someScenario, gameState) = gameAndState

      val scenario = someScenario.addPossibility(Opportunity.victory(trigger, message))

      val (newState, happenings) = Turn.act(scenario)(gameState, trigger)

      happenings.results.contains(ExitGame) :| s"the results were ${happenings.results}"
    }

  // TODO: all turns result either in IDontKnowHowTo OR any number of other things

  // TODO: if IDontKnowHowTo, then state is unchanged

}
