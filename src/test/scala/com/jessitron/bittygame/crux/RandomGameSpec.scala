package com.jessitron.bittygame.crux

import com.jessitron.bittygame.games.RandomGame
import org.scalacheck.{Gen, Properties, Prop}
import org.scalacheck.Prop._

object RandomGameSpec extends Properties("Valid games") {

  val randomGameGen = for {
    optionCount <- Gen.choose(0,10)
    gameDef <- RandomGame.defaultGameGen.funGameGen(optionCount)
  } yield gameDef

  property("a victory condition exists") =
    Prop.forAll(randomGameGen) {
      gameDef: GameDefinition =>
      gameDef.possibilities.exists(_.results.results.contains(Win))
    }


}
