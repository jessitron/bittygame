package com.jessitron.bittygame.crux

import com.jessitron.bittygame.games.RandomGame
import org.scalacheck.{Gen, Properties, Prop}
import org.scalacheck.Prop._
import com.jessitron.bittygame.gen._

object RandomGameProperties extends Properties("Valid games") {

  val randomGameGen = for {
    optionCount <- Gen.choose(0,10)
    gameDef <- RandomGame.defaultGameGen.funGameGen(optionCount)
  } yield gameDef

  property("a victory condition exists") =
    Prop.forAll(randomGameGen :| "game def") {
      gameDef: GameDefinition =>
      gameDef.possibilities.exists(_.results.results.contains(Win))
    }

  property("It never suggests the empty string at first") =
    Prop.forAll(randomGameGen) { gameDef =>
      val (gameState, _) = Turn.firstTurn(gameDef)
      ThinkProperties.neverThinkOfBlank(gameDef, gameState)
    }


}
