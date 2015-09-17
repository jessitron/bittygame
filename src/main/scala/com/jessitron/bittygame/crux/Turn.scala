package com.jessitron.bittygame.crux

import WhatHappens._

object Turn {

  def firstTurn(scenario: Scenario): (GameState, WhatHappens) = {
    val initialState = GameState.init(scenario.title)
    val print = Print(scenario.welcome)
    val exit = if (availableOptions(scenario, initialState).isEmpty) Some(ExitGame) else None
    (initialState, thisHappens(print).andMaybe(exit))
  }

  private def availableOptions(scenario: Scenario, gameState: GameState): Seq[Opportunity] =
    scenario.possibilities.filter(_.available(gameState))

  def act(scenario: Scenario)
         (previousState: GameState, playerTyped: String): (GameState, WhatHappens) = {
    scenario.possibilities.
      filter(_.available(previousState)).
      find(_.triggeredBy(playerTyped)) match {
      case Some(action) =>
        (modifyState(previousState, action), action.take(previousState))
      case None => (previousState, thisHappens(IDontKnowHowTo(playerTyped)))
    }
  }

  private def modifyState(previousState: GameState, action: Opportunity): GameState =
    action.results.results.foldLeft(previousState)(modifyStatePerHappening)

  private def modifyStatePerHappening(previousState: GameState, happening: ThingThatCanHappen) =
    happening match {
      case Acquire(item) => previousState.addToInventory(item)
      case _ => previousState
    }

  def think(scenario: Scenario, gameState: GameState): Seq[String] = {
      scenario.possibilities.map {
        _.trigger
      }
  }

}
