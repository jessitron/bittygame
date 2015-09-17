package com.jessitron.bittygame.crux

import WhatHappens.thisHappens

case class Obstacle(condition: ActionCondition, sadness: MessageToThePlayer) {
  def applies(gameState: GameState) = ActionCondition.met(condition, gameState)
  def results = thisHappens(CantDoThat(sadness))
}

case class Opportunity(trigger: Trigger, results: WhatHappens, conditions: Seq[ActionCondition], obstacles: Seq[Obstacle]) {
  def take(previousState: GameState): WhatHappens =
    obstacles.find(_.applies(previousState)).
      map(obstacle => obstacle.results).
      getOrElse(results)

  def available(gameState: GameState): Boolean = conditions.forall(ActionCondition.met(_, gameState))

  def conflictsWith(other: Opportunity) = other.trigger == trigger

  def behindObstacle(condition: ActionCondition, disappointment: MessageToThePlayer): Opportunity = copy(obstacles = obstacles :+ Obstacle(condition, disappointment))
  def triggeredBy(str: String) = trigger.equalsIgnoreCase(str)
  def andProvides(item: Item) = copy(results = results.and(Acquire(item)))
  def andWin = copy(results = results.and(Win))
  def andExit = copy(results = results.and(ExitGame))
  def onlyIf(condition: ActionCondition) = copy(conditions = conditions :+ condition)
}
object Opportunity {
  def printing(trigger: Trigger, printedResponse: MessageToThePlayer) =
    Opportunity(trigger, thisHappens(Print(printedResponse)), Seq(), Seq())
  def victory(trigger: Trigger, printed: MessageToThePlayer) =
    this.printing(trigger, printed).andWin.andExit
}

case class Scenario(title: ScenarioTitle,
                    possibilities: Seq[Opportunity],
                    welcome: MessageToThePlayer) {
  def addPossibility(a: Opportunity) = {
    assert(!possibilities.exists(_.conflictsWith(a)), "That conflicts with an existing possibility")
    copy(possibilities = possibilities :+ a)
  }
}


