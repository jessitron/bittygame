package com.jessitron.bittygame.crux

case class GameState(inventory: Seq[Item])

object GameState {
  def init: GameState = GameState(Seq())
}
