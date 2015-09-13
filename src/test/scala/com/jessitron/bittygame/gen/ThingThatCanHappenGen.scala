package com.jessitron.bittygame.gen

import com.jessitron.bittygame.crux._
import org.scalacheck.{Gen, Arbitrary}

trait ThingThatCanHappenGen {

  val printGen =
    for {
      str <- Gen.alphaStr
      if str.nonEmpty
    } yield Print(str)

  val thingGen: Gen[ThingThatCanHappen] =
    Gen.oneOf(
      Gen.const(ExitGame),
      printGen,
      Gen.const(Win)
    )

  implicit val arbThing: Arbitrary[ThingThatCanHappen] = Arbitrary(thingGen)

}

object ThingThatCanHappenGen extends ThingThatCanHappenGen
