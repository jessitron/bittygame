package com.jessitron.bittygame.crux

import WhatHappens._

object Turn {

  def firstTurn(gameDef: GameDefinition): (GameState, WhatHappens) = {
    val initialState = GameState.init
    val print = Print(gameDef.welcome)
    val exit = if (availableOptions(gameDef, initialState).isEmpty) Some(ExitGame) else None
    (initialState, thisHappens(print).andMaybe(exit))
  }

  private def availableOptions(gameDef: GameDefinition, gameState: GameState): Seq[PlayerAction] =
    gameDef.possibilities.filter(_.available(gameState))

  def act(gameDef: GameDefinition)
         (previousState: GameState, playerTyped: String): (GameState, WhatHappens) = {
    gameDef.possibilities.find(_.triggeredBy(playerTyped)) match {
      case Some(action) => (previousState, action.results)
      case None => (previousState, NothingHappens)
    }
  }

  def think(gameDef: GameDefinition, gameState: GameState): Seq[String] = {
      gameDef.possibilities.map {
        _.trigger
      }
  }

}
