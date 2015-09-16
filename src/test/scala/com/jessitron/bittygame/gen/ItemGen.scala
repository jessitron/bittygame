package com.jessitron.bittygame.gen

import com.jessitron.bittygame.crux.Item
import org.scalacheck.{Arbitrary, Gen}

trait ItemGen {

  val nonEmptyString = Gen.alphaStr.suchThat(_.nonEmpty)

  val itemGen: Gen[Item] = for {
    itemName <- nonEmptyString
  } yield Item(itemName)

  implicit val arbItem: Arbitrary[Item] = Arbitrary(itemGen)

}
