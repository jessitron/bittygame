package com.jessitron.bittygame.crux

import org.scalacheck.{Prop, Properties}
import com.jessitron.bittygame.gen._
import org.scalacheck.Prop.BooleanOperators

object ActionInventoryProperties extends Properties("Actions that provide inventory") {

  property("If I take an action that provides an item, then I have the item") =
    Prop.forAll {
      (gameDefAndState: (GameDefinition, GameState),
        someAction: PlayerAction,
        item: Item) =>

      val (gameDefWithoutAction, gameState) = gameDefAndState

      val actionProvidingItem = someAction.andProvides(item)
      val gameDef = gameDefWithoutAction.addPossibility(actionProvidingItem)

      val (nextState, _) = Turn.act(gameDef)(gameState, someAction.trigger)

      nextState.hasItem(item) :| "Item not in possession."
    }



}
