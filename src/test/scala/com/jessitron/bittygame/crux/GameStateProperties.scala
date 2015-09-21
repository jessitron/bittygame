package com.jessitron.bittygame.crux

import com.jessitron.bittygame.gen.{ScenarioTitleGen, StatGen}
import org.scalacheck.{Prop, Properties}

object GameStateProperties extends Properties("GameState") with StatGen with ScenarioTitleGen {

  /*
    This property was as easy to write as an example test, since
    the generators existed already.
   */
  property("Stats begin with their starting values") =
    Prop.forAll(scenarioTitleGen, someStatsGen) {
      (title: ScenarioTitle, stats: Seq[Stat]) =>

        val initialState = GameState.init(title, stats)

        stats.forall( s =>
          s.starting == initialState.statValue(s.name)
        )
    }
}
