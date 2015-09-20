package com.jessitron.bittygame.crux

import com.jessitron.bittygame.gen.{GameStateGen, StatGen}
import org.scalacheck.{Gen, Prop, Properties}
import org.scalacheck.Prop.BooleanOperators

object ConditionProperties extends Properties("Conditionals are the backbone of programming") with StatGen with GameStateGen {


  private val HighEnoughConditionNotMetByState: Gen[(Condition, GameState)] = for {
    stat <- statGen
    bar <- Gen.choose(minStatLow + 1, maxStatHigh)
    condition = MustBeHighEnough(stat.name, bar)
    myLevel <- Gen.choose(minStatLow, bar - 1)
    someState <- gameStateGen("a game of Lizards", Seq(), Seq(stat))
    state = someState.newStat(stat.name, myLevel)
  } yield (condition, state)


  property("not high enough, out of luck") = Prop.forAll(
    HighEnoughConditionNotMetByState
  )
  { case (condition, state) =>
    (Condition.met(condition, state) == false) :| s"Should not be able to at ${state.stats}, wanted $condition"
  }

}
