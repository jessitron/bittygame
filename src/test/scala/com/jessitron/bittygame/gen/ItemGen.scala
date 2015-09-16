package com.jessitron.bittygame.gen

import com.jessitron.bittygame.crux.Item
import org.scalacheck.{Arbitrary, Gen}

trait NonEmptyStringGen {

  // I tried to give it all unicode chars, but yikes that gives some weird failures
  val allTheChars: Gen[Char] = Gen.oneOf(Gen.alphaNumChar, Gen.oneOf(' ', '@',',','-'))

  // this is slow, but it never fails
  val shortStringOfAnything = for {
    howMany <- Gen.choose(1,10)
    chars <- Gen.listOfN(howMany, allTheChars)
  } yield new String(chars.toArray)

  val nonEmptyString = shortStringOfAnything.suchThat(_.nonEmpty) // this for shrinking
}

trait ItemGen extends NonEmptyStringGen {

  val itemGen: Gen[Item] = for {
    itemName <- nonEmptyString
  } yield Item(itemName)

  implicit val arbItem: Arbitrary[Item] = Arbitrary(itemGen)

  val someItems: Gen[Seq[Item]] = Gen.resize(10, Gen.listOf(itemGen))
}
