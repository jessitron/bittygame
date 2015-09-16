package com.jessitron.bittygame.crux

import com.jessitron.bittygame.gen.GameStateGen
import org.scalacheck.{Prop, Properties}

object ThinkProperties extends Properties("of thinking") with GameStateGen {

  val neverThinkOfBlank: PartialFunction[(Scenario, GameState), Prop] =
  {
    case (scenario, gameState) =>
      !Turn.think(scenario, gameState).contains("")
  }

  property("Never returns a blank option") =
    Prop.forAll(scenarioAndStateGen)(neverThinkOfBlank)
}
