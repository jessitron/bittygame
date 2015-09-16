package com.jessitron.bittygame.crux

import WhatHappens.thisHappens

case class PlayerAction(trigger: Trigger, results: WhatHappens, acquire: Seq[Item]) {
  def available(gameState: GameState): Boolean = true
  def triggeredBy(str: String) = trigger.equalsIgnoreCase(str)
  def andProvides(item: Item) = copy(acquire = acquire :+ item)

  def conflictsWith(other: PlayerAction) = other.trigger == trigger
}
object PlayerAction {
  def printing(trigger: Trigger, printedResponse: MessageToThePlayer) =
    PlayerAction(trigger, thisHappens(Print(printedResponse)), acquire = Seq())
  def victory(trigger: Trigger, printed: MessageToThePlayer) =
    PlayerAction(trigger, thisHappens(Print(printed)).and(Win).and(ExitGame), acquire = Seq())
}

case class GameDefinition(possibilities: Seq[PlayerAction],
                          welcome: MessageToThePlayer) {
  def addPossibility(a: PlayerAction) = copy(possibilities = possibilities :+ a)
}


