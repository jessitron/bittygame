package com.jessitron.bittygame.crux

case class GameState(inventory: Seq[Item]) {
  def hasItem(it: Item): Boolean = inventory.contains(it)
}

object GameState {
  def init: GameState = GameState(Seq())
}
