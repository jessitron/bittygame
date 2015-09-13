package com.jessitron.bittygame.crux

import org.scalacheck.{Prop, Properties}
import com.jessitron.bittygame.gen.gameAndStateGen

object ThinkProperties extends Properties("of thinking") {

  val neverThinkOfBlank: PartialFunction[(GameDefinition, GameState), Prop] =
  {
    case (gameDef, gameState) =>
      !Turn.think(gameDef, gameState).contains("")
  }

  property("Never returns a blank option") =
    Prop.forAll(gameAndStateGen)(neverThinkOfBlank)
}
