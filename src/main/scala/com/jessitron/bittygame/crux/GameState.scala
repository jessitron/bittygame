package com.jessitron.bittygame.crux


case class GameState(title: ScenarioTitle, inventory: Seq[Item], stats : Map[StatID, Int]) {
  def newStat(name: StatID, myLevel: Int) = copy(stats = stats + (name -> myLevel))

  def increase(name: StatID) = copy(stats = stats.updated(name, stats(name) + 1))

  def addToInventory(item: Item) = copy(inventory = inventory :+ item)

  def hasItem(it: Item): Boolean = inventory.contains(it)

  def statValue(name: StatID) =
    stats.getOrElse(name, throw new RuntimeException("Stat not found: " + name))
}

object GameState {
  // TODO: property -- all stats start at their starting value
  def init(scenarioKey: ScenarioTitle, stats: Seq[Stat]): GameState = GameState(scenarioKey, Seq(), Map())
}
