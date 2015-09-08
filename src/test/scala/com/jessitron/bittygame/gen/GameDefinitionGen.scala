package com.jessitron.bittygame.gen

import com.jessitron.bittygame.crux.{PlayerAction, GameDefinition}
import org.scalacheck.Arbitrary
import org.scalacheck.Gen

trait GameDefinitionGen {
  
  val playerActionGen: Gen[PlayerAction] = for {
    trigger <- Gen.alphaStr
    message <- Gen.alphaStr
  } yield PlayerAction(trigger, message)
  
  val possibilitiesGen: Gen[Seq[PlayerAction]] = Gen.listOf(playerActionGen)

  val welcomeMessageGen = Gen.alphaStr.withFilter(_.nonEmpty)

  val gameDefGen = for {
    possibilities <- possibilitiesGen
    welcome <- welcomeMessageGen
  } yield GameDefinition(possibilities, welcome)

  implicit val arbitraryGameDef: Arbitrary[GameDefinition] = Arbitrary(gameDefGen)

}
object GameDefinitionGen extends GameDefinitionGen
