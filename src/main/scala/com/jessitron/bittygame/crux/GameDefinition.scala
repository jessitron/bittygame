package com.jessitron.bittygame.crux

import WhatHappens.thisHappens

case class PlayerAction(trigger: Trigger, results: WhatHappens, conditions: Seq[ActionCondition]) {
  def available(gameState: GameState): Boolean = conditions.forall(ActionCondition.met(_, gameState))
  def conflictsWith(other: PlayerAction) = other.trigger == trigger

  def triggeredBy(str: String) = trigger.equalsIgnoreCase(str)
  def andProvides(item: Item) = copy(results = results.and(Acquire(item)))
  def onlyIf(condition: ActionCondition) = copy(conditions = conditions :+ condition)
}
object PlayerAction {
  def printing(trigger: Trigger, printedResponse: MessageToThePlayer) =
    PlayerAction(trigger, thisHappens(Print(printedResponse)), Seq())
  def victory(trigger: Trigger, printed: MessageToThePlayer) =
    PlayerAction(trigger, thisHappens(Print(printed)).and(Win).and(ExitGame), Seq())
}

case class GameDefinition(possibilities: Seq[PlayerAction],
                          welcome: MessageToThePlayer) {
  def addPossibility(a: PlayerAction) = copy(possibilities = possibilities :+ a)
}


