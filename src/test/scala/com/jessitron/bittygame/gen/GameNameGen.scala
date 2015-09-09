package com.jessitron.bittygame.gen

import org.scalacheck.Gen

trait GameNameGen {

  val gameNameGen: Gen[String] = Gen.alphaStr.suchThat(_.nonEmpty)

}
