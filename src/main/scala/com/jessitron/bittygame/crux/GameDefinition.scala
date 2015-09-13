package com.jessitron.bittygame.crux

import WhatHappens.thisHappens

case class PlayerAction(trigger: Trigger, results: WhatHappens) {
  def available(gameState: GameState): Boolean = true
  def triggeredBy(str: String) = trigger.equalsIgnoreCase(str)
}
object PlayerAction {
  def printing(trigger: Trigger, printedResponse: MessageToThePlayer) =
    PlayerAction(trigger, thisHappens(Print(printedResponse)))
  def victory(trigger: Trigger, printed: MessageToThePlayer) =
    PlayerAction(trigger, thisHappens(Print(printed)).and(Win).and(ExitGame))
}

case class GameDefinition(possibilities: Seq[PlayerAction],
                          welcome: MessageToThePlayer) {
  def addPossibility(a: PlayerAction) = copy(possibilities = possibilities :+ a)
}


