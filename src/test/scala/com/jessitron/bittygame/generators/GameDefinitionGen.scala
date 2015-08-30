package com.jessitron.bittygame.generators

import com.jessitron.bittygame.GameDefinition
import org.scalacheck.Arbitrary
import org.scalacheck.Gen

trait GameDefinitionGen {

  val gameDefGen = Gen.const(GameDefinition(Seq()))

  implicit val arbitraryGameDef: Arbitrary[GameDefinition] = Arbitrary(gameDefGen)

}
object GameDefinitionGen extends GameDefinitionGen
