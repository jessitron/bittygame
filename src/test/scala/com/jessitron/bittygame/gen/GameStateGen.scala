package com.jessitron.bittygame.gen

import com.jessitron.bittygame.crux._
import org.scalacheck.util.Pretty
import org.scalacheck.{Arbitrary, Gen}

trait GameStateGen extends ItemGen with StatGen {

  def gameStateGen(title : ScenarioTitle, items: Seq[Item], stats: Seq[Stat]): Gen[GameState] =
    for {
      items <- Gen.someOf(items)
      statValuePairs <- startingValuesGen(stats)
    } yield GameState(title, items, Map(statValuePairs:_*))


    val independentGameStateGen : Gen[GameState] =
      for {
        itemCount <- Gen.choose(0,4)
        items <- Gen.listOfN(itemCount, itemGen)
        someStats <- someStatsGen
        statValues <- startingValuesGen(someStats)
      } yield GameState("Some game or other", items, Map(statValues:_*) )


}
