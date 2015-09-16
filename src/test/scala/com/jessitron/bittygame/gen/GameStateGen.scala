package com.jessitron.bittygame.gen

import com.jessitron.bittygame.crux.{GameState, GameDefinition}
import org.scalacheck.{Arbitrary, Gen}

trait GameStateGen extends GameDefinitionGen {

  def gameStateGen(gameDef: GameDefinition): Gen[GameState] = Gen.const(GameState.init) // TODO

  def gameAndStateGen: Gen[(GameDefinition, GameState)] =
    for {
      gameDef <- gameDefGen
      gameState <- gameStateGen(gameDef)
    } yield (gameDef, gameState)

  implicit val arbGameAndState: Arbitrary[(GameDefinition, GameState)] = Arbitrary(gameAndStateGen)

}
