package com.jessitron.bittygame.crux

import WhatHappens._

object Turn {

  def firstTurn(gameDef: GameDefinition): (GameState, WhatHappens) =
    (GameState.init, thisHappens(Print(gameDef.welcome)).and(ExitGame))

  def act(gameDef: GameDefinition)
         (previousState: GameState, playerTyped: String): (GameState, WhatHappens) = {
    (previousState, NothingHappens)
  }

}
