package com.jessitron.bittygame.gen

import com.jessitron.bittygame.crux._
import org.scalacheck.util.Pretty
import org.scalacheck.{Gen, Arbitrary}
import scala.util.Random.shuffle

trait ThingThatCanHappenGen extends ItemGen {

  val printGen =
    for {
      str <- Gen.alphaStr
      if str.nonEmpty
    } yield Print(str)

  val exitGen = Gen.const(ExitGame)
  val winGen = Gen.const(Win)
  val getAThingGen = itemGen.map(Acquire(_))
  val dontKnowHowGen = nonEmptyString.map(IDontKnowHowTo(_))
  val cantDoItGen = nonEmptyString.map(CantDoThat(_))

  val thingGen: Gen[ThingThatCanHappen] =
    Gen.oneOf(
      exitGen,
      printGen,
      winGen,
      getAThingGen,
      dontKnowHowGen,
      cantDoItGen
    )

  implicit val arbThing: Arbitrary[ThingThatCanHappen] = Arbitrary(thingGen)
  
  val takenOpportunityGen = for {
    howMany <- Gen.choose(1,6)
    one <- printGen
    two <- winGen
    three <- exitGen
    howManyItems <- Gen.choose(1, howMany)
    itemsToAcquire <- Gen.listOfN(howManyItems, getAThingGen)
  } yield {
      val stuff = shuffle(Seq(one,two,three) ++ itemsToAcquire).take(howMany)
      WhatHappens(stuff)
    }
  
  val anythingCouldHappenGen =
    Gen.oneOf(
      takenOpportunityGen,
      dontKnowHowGen.map(WhatHappens.thisHappens),
      cantDoItGen.map(WhatHappens.thisHappens)
    )

  implicit val arbWhatHappens: Arbitrary[WhatHappens] = Arbitrary(takenOpportunityGen)

  implicit def prettyWhatHappens(wh: WhatHappens):Pretty = Pretty { p =>
    wh.results.map {
      case Print(str) => s"print '$str'"
      case ExitGame => "Exit!"
      case Win => "WIN!!"
      case Acquire(item) => s"Get $item"
    }.mkString("\n      and ")
  }

}

object ThingThatCanHappenGen extends ThingThatCanHappenGen
