package com.jessitron.bittygame.crux

import org.scalacheck.{Prop, Properties}
import com.jessitron.bittygame.gen.gameAndStateGen

object ThinkProperties extends Properties("of thinking") {

  val neverThinkOfBlank: PartialFunction[(Scenario, GameState), Prop] =
  {
    case (scenario, gameState) =>
      !Turn.think(scenario, gameState).contains("")
  }

  property("Never returns a blank option") =
    Prop.forAll(gameAndStateGen)(neverThinkOfBlank)
}
