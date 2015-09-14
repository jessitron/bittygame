package com.jessitron.bittygame.gen

import com.jessitron.bittygame.crux._
import org.scalacheck.util.Pretty
import org.scalacheck.{Gen, Arbitrary}
import scala.util.Random.shuffle

trait ThingThatCanHappenGen {

  val printGen =
    for {
      str <- Gen.alphaStr
      if str.nonEmpty
    } yield Print(str)

  val exitGen = Gen.const(ExitGame)
  val winGen = Gen.const(Win)

  val thingGen: Gen[ThingThatCanHappen] =
    Gen.oneOf(
      exitGen,
      printGen,
      winGen
    )

  implicit val arbThing: Arbitrary[ThingThatCanHappen] = Arbitrary(thingGen)

  val whatHappensGen = for {
    howMany <- Gen.choose(1,3)
    one <- printGen
    two <- winGen
    three <- exitGen
  } yield {
      val stuff = shuffle(Seq(one,two,three)).take(howMany)
      WhatHappens(stuff)
    }

  implicit val arbWhatHappens: Arbitrary[WhatHappens] = Arbitrary(whatHappensGen)

  implicit def prettyWhatHappens(wh: WhatHappens):Pretty = Pretty { p =>
    wh.results.map {
      case Print(str) => s"print '$str'"
      case ExitGame => "Exit!"
      case Win => "WIN!!"
    }.mkString("\n      and ")
  }

}

object ThingThatCanHappenGen extends ThingThatCanHappenGen
