package com.jessitron.bittygame.crux


case class GameState(title: ScenarioTitle,
                     inventory: Seq[Item],
                     stats : Map[StatID, Int]) {

  def addToInventory(item: Item) = copy(inventory = inventory :+ item)

  def hasItem(it: Item): Boolean = inventory.contains(it)

  def newStat(name: StatID, myLevel: Int) = copy(stats = stats + (name -> myLevel))

  def increase(name: StatID) = copy(stats = stats.updated(name, stats(name) + 1))

  def statValue(name: StatID) =
    stats.getOrElse(name, throw new RuntimeException("Stat not found: " + name))
}

object GameState {
  def init(scenarioKey: ScenarioTitle, stats: Seq[Stat]): GameState =
    GameState(scenarioKey, Seq(), Map(stats.map{s => s.name -> s.starting } :_*))
}
