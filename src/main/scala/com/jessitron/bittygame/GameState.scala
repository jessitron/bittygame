package com.jessitron.bittygame

case class GameState(inventory: Seq[Item])

object GameState {
  def init: GameState = GameState(Seq())
}
