package com.jessitron.bittygame.gen

import com.jessitron.bittygame.crux.GameDefinition
import org.scalacheck.Arbitrary
import org.scalacheck.Gen

trait GameDefinitionGen {

  val welcomeMessageGen = Gen.alphaStr.withFilter(_.nonEmpty)

  val gameDefGen = for {
    possibilities <- Gen.const(Seq())
    welcome <- welcomeMessageGen
  } yield GameDefinition(possibilities, welcome)

  implicit val arbitraryGameDef: Arbitrary[GameDefinition] = Arbitrary(gameDefGen)

}
object GameDefinitionGen extends GameDefinitionGen
