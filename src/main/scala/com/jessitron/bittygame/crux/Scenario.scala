package com.jessitron.bittygame.crux

import WhatHappens.thisHappens

case class Opportunity(trigger: Trigger, results: WhatHappens, conditions: Seq[ActionCondition]) {
  def available(gameState: GameState): Boolean = conditions.forall(ActionCondition.met(_, gameState))
  def conflictsWith(other: Opportunity) = other.trigger == trigger

  def triggeredBy(str: String) = trigger.equalsIgnoreCase(str)
  def andProvides(item: Item) = copy(results = results.and(Acquire(item)))
  def onlyIf(condition: ActionCondition) = copy(conditions = conditions :+ condition)
}
object Opportunity {
  def printing(trigger: Trigger, printedResponse: MessageToThePlayer) =
    Opportunity(trigger, thisHappens(Print(printedResponse)), Seq())
  def victory(trigger: Trigger, printed: MessageToThePlayer) =
    Opportunity(trigger, thisHappens(Print(printed)).and(Win).and(ExitGame), Seq())
}

case class Scenario(possibilities: Seq[Opportunity],
                          welcome: MessageToThePlayer) {
  def addPossibility(a: Opportunity) = copy(possibilities = possibilities :+ a)
}


