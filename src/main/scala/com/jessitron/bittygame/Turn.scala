package com.jessitron.bittygame

import WhatHappens.NothingHappens

object Turn {

  def firstTurn(gameDef: GameDefinition): (GameState, WhatHappens) =
    (GameState.init, NothingHappens)

  def act(gameDef: GameDefinition)
         (previousState: GameState, playerTyped: String): (GameState, WhatHappens) = {
    (previousState, NothingHappens)
  }

}
