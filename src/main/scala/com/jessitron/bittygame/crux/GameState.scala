package com.jessitron.bittygame.crux

case class GameState(inventory: Seq[Item]) {
  def addToInventory(item: Item) = copy(inventory = inventory :+ item)

  def hasItem(it: Item): Boolean = inventory.contains(it)
}

object GameState {
  def init: GameState = GameState(Seq())
}
