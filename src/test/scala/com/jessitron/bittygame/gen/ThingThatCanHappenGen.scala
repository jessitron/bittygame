package com.jessitron.bittygame.gen

import com.jessitron.bittygame.crux._
import org.scalacheck.{Gen, Arbitrary}

trait ThingThatCanHappenGen {

  val printGen = Gen.alphaStr.map(Print.apply)

  val thingGen: Gen[ThingThatCanHappen] = Gen.oneOf(Gen.const(ExitGame), printGen)

  implicit val arbThing: Arbitrary[ThingThatCanHappen] = Arbitrary(thingGen)

}

object ThingThatCanHappenGen extends ThingThatCanHappenGen
