package com.jessitron.bittygame.crux

import com.jessitron.bittygame.games.RandomGame
import org.scalacheck.util.Pretty
import org.scalacheck.{Gen, Properties, Prop}
import org.scalacheck.Prop._

object RandomGameSpec extends Properties("Valid games") {

  val randomGameGen = for {
    optionCount <- Gen.choose(0,10)
    gameDef <- RandomGame.defaultGameGen.funGameGen(optionCount)
  } yield gameDef

  implicit def prettyWhatHappens(wh: WhatHappens):Pretty = Pretty { p =>
    wh.results.map {
      case Print(str) => s"print '$str'"
      case ExitGame => "Exit!"
      case Win => "WIN!!"
    }.mkString("\n      and ")
  }

  implicit def prettyPlayerAction(pa: PlayerAction):Pretty = Pretty { p =>
    s"Type ${pa.trigger} to " + prettyWhatHappens(pa.results)(p)
  }

  implicit def prettyGameDef(g: GameDefinition): Pretty = Pretty { p =>
    s"GameDefinition: \n${g.welcome}\n" +
      g.possibilities.map(prettyPlayerAction(_)(p)).map("  " + _).mkString("\n")
  }

  property("a victory condition exists") =
    Prop.forAll(randomGameGen :| "game def") {
      gameDef: GameDefinition =>
      gameDef.possibilities.exists(_.results.results.contains(Win))
    }


}
