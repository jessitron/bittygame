package com.jessitron.bittygame.crux

import com.jessitron.bittygame.gen._
import org.scalacheck.{Properties, Prop}
import org.scalacheck.Prop.BooleanOperators
import org.scalatest.{Assertions, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

object TurnProperties extends Properties("Taking a turn") with GameStateGen {

  property("Victory actions result in exit") =
    Prop.forAll(scenarioAndStateGen, triggerGen, messageGen) {
      (gameAndState, trigger, message) =>
        val (someScenario, gameState) = gameAndState

        val win = Opportunity.victory(trigger, message)

        noConflict(someScenario, win) ==> {

          val scenario = someScenario.addPossibility(win)

          val (newState, happenings) = Turn.act(scenario)(gameState, trigger)

          happenings.results.contains(ExitGame) :| s"the results were ${happenings.results}"
        }
    }

  def noConflict(scenario: Scenario, opportunity: Opportunity): Boolean =
    !scenario.opportunities.exists(_.conflictsWith(opportunity))

 def noStatConflict(scenario: Scenario, stat: Stat): Boolean =
    !scenario.stats.exists(_.conflictsWith(stat))

  property("An opportunity can increase a stat") =
    Prop.forAll(
      for {
        someScenario <- scenarioGen
        stat <- statGen
        if noStatConflict(someScenario, stat)
        scenario = someScenario.addStat(stat)
        state <- gameStateGen(scenario)
        opp <- alwaysAvailableOpportunity(scenario.stats)
        if noConflict(scenario, opp)
      } yield (scenario, opp, stat, state)
    ) {
      case (scenario: Scenario, opp : Opportunity, stat: Stat, state: GameState) =>
        val levelUpOpportunity = opp.andIncrease(stat.name)
        val scenarioWithOpportunity = scenario.addPossibility(levelUpOpportunity)

        val (newState, happenings) = Turn.act(scenarioWithOpportunity)(state, levelUpOpportunity.trigger)

        val newValue = newState.statValue(stat.name)
        val oldValue = state.statValue(stat.name)
        ((newValue == 1 + oldValue) || (newValue == stat.high)) :| s"Poo. $newValue should be or more than $oldValue or else ${stat.high}"
        // after seeing a failure, check whether the stat's initial value is its top value
      }


  val neverThinkOfBlank: ((Scenario, GameState)) => Prop = {
    case (scenario, gameState) =>
      !Turn.think(scenario, gameState).contains("")
  }

  property("Never returns a blank option") =
    Prop.forAll(scenarioAndStateGen)(neverThinkOfBlank)

  // TODO: all turns result either in IDontKnowHowTo OR any number of other things

  // TODO: if IDontKnowHowTo, then state is unchanged

}

object FirstTurnProperties
  extends PropSpec
  with GeneratorDrivenPropertyChecks
  with Assertions
  with GameStateGen {
  {
    property("The game starts with an empty inventory") {
      forAll { (scenario: Scenario) =>
        val (initialState, happenings) = Turn.firstTurn(scenario)
        assert(initialState.inventory.isEmpty, "inventory should be empty on first turn")
      }
    }

    property("The game starts with some message") {
      forAll { (scenario: Scenario) =>
        val (initialState, happenings) = Turn.firstTurn(scenario)

        val printedMessages = happenings.results.collect {
          case Print(msg) => msg
        }
        assert(printedMessages.nonEmpty, "every game should print something")
      }
    }

    property("Iff there are no available options, exit on first turn") {
      forAll { scenario: Scenario =>
        forAll(gameStateGen(scenario)) { gameState: GameState =>
          val (_, happenings) = Turn.firstTurn(scenario)
          val anythingAvailable: Boolean = scenario.opportunities.exists(_.available(gameState))
          val autoExit: Boolean = happenings.results.contains(ExitGame)
          assert(autoExit == !anythingAvailable,
            s"Should only exit if no options are available. Exiting? $autoExit Available? $anythingAvailable")
        }
      }
    }
  }
}

