package com.jessitron.bittygame.crux

import com.jessitron.bittygame.gen._
import org.scalacheck.Prop
import org.scalacheck.Prop.BooleanOperators

object TurnTest extends org.scalacheck.Properties("Taking a turn") {

  property("Victory actions result in exit") =
    Prop.forAll(gameAndStateGen, triggerGen, messageGen ) { (gameAndState, trigger, message) =>
      val (gameDef, gameState) = gameAndState
      val victoryAction = PlayerAction.victory(trigger, message)
      gameDef.addPossibility(victoryAction)

      val (newState, happenings) = Turn.act(gameDef)(gameState, trigger)

      happenings.results.contains(ExitGame) :| s"the results were ${happenings.results}"
    }

}
