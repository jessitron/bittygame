package com.jessitron.bittygame.crux

sealed trait Condition
case class Has(item: Item) extends Condition


object Condition {

  def met(condition: Condition, gameState: GameState): Boolean =
    condition match {
      case Has(item) if gameState.hasItem(item) => true
      case _ => false
    }
}
