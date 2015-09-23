package com.jessitron.bittygame.crux

sealed trait Condition
case class Has(item: Item) extends Condition
case class NotHas(item: Item) extends Condition
case class MustBeHighEnough(stat: StatID, value: Int) extends Condition

object Condition {

  def met(condition: Condition, gameState: GameState): Boolean =
    condition match {
      case Has(item)    =>  gameState.hasItem(item)
      case NotHas(item) => !gameState.hasItem(item)
      case MustBeHighEnough(stat, level) => gameState.statValue(stat) >= level
      case _ => false
    }
}
