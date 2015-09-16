package com.jessitron.bittygame.crux

sealed trait ActionCondition
case class Has(item: Item) extends ActionCondition


object ActionCondition {

  def met(condition: ActionCondition, gameState: GameState): Boolean =
    condition match {
      case Has(item) if gameState.hasItem(item) => true
      case _ => false
    }
}
