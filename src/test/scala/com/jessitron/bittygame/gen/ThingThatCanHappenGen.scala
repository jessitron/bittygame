package com.jessitron.bittygame.gen

import java.lang.Math.min

import com.jessitron.bittygame.crux._
import org.scalacheck.util.Pretty
import org.scalacheck.{Shrink, Gen, Arbitrary}
import scala.util.Random.shuffle

trait ThingThatCanHappenGen extends ItemGen with StatGen {

  val printGen = nonEmptyString.map(Print(_))

  val exitGen = Gen.const(ExitGame)
  val winGen = Gen.const(Win)
  val getAThingGen = itemGen.map(Acquire(_))
  val dontKnowHowGen = nonEmptyString.map(IDontKnowHowTo(_))
  val cantDoItGen = nonEmptyString.map(CantDoThat(_))
  // TODO: add increase stat

  val thingGen: Gen[TurnResult] =
    Gen.oneOf(
      exitGen,
      printGen,
      winGen,
      getAThingGen,
      dontKnowHowGen,
      cantDoItGen
    )

  implicit val arbThing: Arbitrary[TurnResult] = Arbitrary(thingGen)

  def acquireSomeOf(noMoreThan: Int, itemsInGame: Seq[Item]) = for {
    howManyItems <- Gen.choose(1, min(noMoreThan, itemsInGame.length))
    acquireAnItem = Gen.oneOf(itemsInGame).map(Acquire(_))
    itemsToAcquire <- Gen.listOfN(howManyItems, acquireAnItem)
  } yield itemsToAcquire
  
  def thingsThatHappenWhenYouTakeAnOpportunityGen(itemsInGame: Seq[Item], statsInGame: Seq[Stat]): Gen[WhatHappens] =
    for {
      howMany <- Gen.choose(1,6)
      one <- printGen
      two <- winGen
      three <- exitGen
      more <- acquireSomeOf(howMany, itemsInGame)
      increases <- Gen.someOf(howMany, statsInGame.map(st => IncreaseStat(st.name)))
    } yield {
      val stuff = shuffle(Seq(one,two,three) ++ more).take(howMany)
      WhatHappens(stuff)
    }

  implicit val whatHappensShrink: Shrink[WhatHappens] = Shrink {
    orig =>
      Shrink.shrink(orig.results).filter(_.nonEmpty).map(x => orig.copy(results = x))
  }
  
  val anythingCouldHappenGen =
    for {
      aListOfItems <- someItemsGen
      aListOfStats <- someStatsGen
      something <- Gen.oneOf(
        thingsThatHappenWhenYouTakeAnOpportunityGen(aListOfItems, aListOfStats),
        dontKnowHowGen.map(WhatHappens.thisHappens),
        cantDoItGen.map(WhatHappens.thisHappens)
      )
    } yield something

  def printWhatHappens(wh: WhatHappens) = wh.results.map {
    case Print(str) => s"print '$str'"
    case ExitGame => "Exit!"
    case Win => "WIN!!"
    case Acquire(item) => s"Get $item"
    case CantDoThat(s) => s"Can't do that because $s"
    case IDontKnowHowTo(s) => s"Unknown option: $s"
    case IncreaseStat(s) => s"Level up: $s"
  }.mkString("\n      and ")

  implicit def prettyWhatHappens(wh: WhatHappens):Pretty = Pretty { p =>
    printWhatHappens(wh)
  }

}

object ThingThatCanHappenGen extends ThingThatCanHappenGen
