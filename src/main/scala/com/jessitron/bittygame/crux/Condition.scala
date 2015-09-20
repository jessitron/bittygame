package com.jessitron.bittygame.crux

sealed trait Condition
case class Has(item: Item) extends Condition
case class MustBeHighEnough(stat: StatID, value: Int) extends Condition

object Condition {

  def met(condition: Condition, gameState: GameState): Boolean =
    condition match {
      case Has(item) if gameState.hasItem(item) => true
      case MustBeHighEnough(stat, level)  => gameState.statValue(stat) >= level
      case _ => false
    }
}
gs