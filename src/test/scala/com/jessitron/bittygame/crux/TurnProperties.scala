package com.jessitron.bittygame.crux

import com.jessitron.bittygame.gen._
import org.scalacheck.Prop
import org.scalacheck.Prop.BooleanOperators

object TurnProperties extends org.scalacheck.Properties("Taking a turn") {

  property("Victory actions result in exit") =
    Prop.forAll(gameAndStateGen, triggerGen, messageGen ) { (gameAndState, trigger, message) =>
      val (someGame, gameState) = gameAndState

      val gameDef = someGame.addPossibility(PlayerAction.victory(trigger, message))

      val (newState, happenings) = Turn.act(gameDef)(gameState, trigger)

      happenings.results.contains(ExitGame) :| s"the results were ${happenings.results}"
    }

  // TODO: all turns result either in IDontKnowHowTo OR any number of other things

  // TODO: if IDontKnowHowTo, then state is unchanged

}
