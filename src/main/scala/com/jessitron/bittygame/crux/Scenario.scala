package com.jessitron.bittygame.crux

import WhatHappens.thisHappens

case class Obstacle(condition: Condition, sadness: MessageToThePlayer) {
  def applies(gameState: GameState) = Condition.met(condition, gameState)
  def results = thisHappens(CantDoThat(sadness))
}

case class Opportunity(trigger: Trigger, results: WhatHappens, conditions: Seq[Condition], obstacles: Seq[Obstacle]) {

  def take(previousState: GameState): WhatHappens =
    obstacles.find(_.applies(previousState)).
      map(obstacle => obstacle.results).
      getOrElse(results)

  def available(gameState: GameState): Boolean = conditions.forall(Condition.met(_, gameState))
  def conflictsWith(other: Opportunity) = other.trigger == trigger
  def triggeredBy(str: String) = trigger.equalsIgnoreCase(str)
  def willExit: Boolean = results.results.contains(ExitGame)

  def behindObstacle(condition: Condition, disappointment: MessageToThePlayer): Opportunity = copy(obstacles = obstacles :+ Obstacle(condition, disappointment))
  def andProvides(item: Item) = copy(results = results.and(Acquire(item)))
  def andWin = copy(results = results.and(Win))
  def andExit = copy(results = results.and(ExitGame))
  def onlyIf(condition: Condition) = copy(conditions = conditions :+ condition)
  def andIncrease(stat: StatID) = copy(results = results.and(IncreaseStat(stat)))
}
object Opportunity {
  def printing(trigger: Trigger, printedResponse: MessageToThePlayer) =
    Opportunity(trigger, thisHappens(Print(printedResponse)), Seq(), Seq())
  def victory(trigger: Trigger, printed: MessageToThePlayer) =
    this.printing(trigger, printed).andWin.andExit
}

case class Scenario(title: ScenarioTitle,
                    opportunities: Seq[Opportunity],
                    welcome: MessageToThePlayer,
                    stats: Seq[Stat],
                    items: Seq[Item]) {
  def addPossibility(a: Opportunity) = {
    assert(!opportunities.exists(_.conflictsWith(a)), "That conflicts with an existing possibility")
    copy(opportunities = opportunities :+ a)
  }
  def addStat(s: Stat) = {
    assert(!stats.map(_.name).contains(s.name), "Conflict with existing stat: " + s.name)
    copy(stats = stats :+ s)
  }

  override def toString =
    s"Scenario: $title\n  Welcome: ${this.welcome}\n" +
      this.opportunities.map(_.toString).map("  " + _).mkString("\n") +
      s"\n  Stats: ${this.stats}\n"
}


