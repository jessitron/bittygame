package com.jessitron.bittygame.crux

import WhatHappens._

object Turn {

  def firstTurn(scenario: Scenario): (GameState, WhatHappens) = {
    val initialState = GameState.init(scenario.title, scenario.stats)
    val print = Print(scenario.welcome)
    val exit = if (availableOptions(scenario, initialState).isEmpty) Some(ExitGame) else None
    (initialState, thisHappens(print).andMaybe(exit))
  }

  def act(scenario: Scenario)
         (previousState: GameState, playerTyped: String): (GameState, WhatHappens) = {
    availableOptions(scenario, previousState).
      find(_.triggeredBy(playerTyped)) match {
      case Some(action) =>
        (modifyState(previousState, action), action.take(previousState))
      case None =>
        (previousState, thisHappens(IDontKnowHowTo(playerTyped)))
    }
  }

  def think(scenario: Scenario, gameState: GameState): Seq[String] = {
    availableOptions(scenario, gameState).map {
      _.trigger
    }
  }

  private def availableOptions(scenario: Scenario, gameState: GameState): Seq[Opportunity] =
    scenario.opportunities.filter(_.available(gameState))

  private def modifyState(previousState: GameState, action: Opportunity): GameState =
    action.results.results.foldLeft(previousState)(modifyStatePerHappening)

  private def modifyStatePerHappening(previousState: GameState, happening: TurnResult) =
    happening match {
      case Acquire(item) => previousState.addToInventory(item)
      case IncreaseStat(statName) => previousState.increase(statName)
      case _ => previousState
    }


}
