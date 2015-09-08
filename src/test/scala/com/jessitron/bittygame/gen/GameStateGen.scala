package com.jessitron.bittygame.gen

import com.jessitron.bittygame.crux.{GameState, GameDefinition}
import org.scalacheck.Gen

trait GameStateGen {

  def gameStateGen(gameDef: GameDefinition): Gen[GameState] = Gen.const(GameState.init) // TODO

}
