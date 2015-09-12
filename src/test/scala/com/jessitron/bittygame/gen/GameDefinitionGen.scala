package com.jessitron.bittygame.gen

import com.jessitron.bittygame.crux.{MessageToThePlayer, Trigger, PlayerAction, GameDefinition}
import org.scalacheck.Arbitrary
import org.scalacheck.Gen

trait PlayerActionGen {

  val triggerGen: Gen[Trigger] = Gen.alphaStr.suchThat(_.nonEmpty)

  val messageGen: Gen[MessageToThePlayer] = Gen.alphaStr.suchThat(_.nonEmpty)

  val victoryActionGen: Gen[PlayerAction] =
  for {
    trigger <- triggerGen
    message <- messageGen
  } yield PlayerAction.victory(trigger, message)

  val printActionGen: Gen[PlayerAction] = for {
    trigger <- triggerGen
    message <- messageGen
  } yield PlayerAction.printing(trigger, message)

  val playerActionGen = Gen.frequency((4,printActionGen), (1,victoryActionGen))
}

trait GameDefinitionGen extends PlayerActionGen {

  val possibilitiesGen: Gen[Seq[PlayerAction]] = Gen.listOf(playerActionGen)

  val welcomeMessageGen = Gen.alphaStr.withFilter(_.nonEmpty)

  val gameDefGen = for {
    possibilities <- possibilitiesGen
    welcome <- welcomeMessageGen
  } yield GameDefinition(possibilities, welcome)

  implicit val arbitraryGameDef: Arbitrary[GameDefinition] = Arbitrary(gameDefGen)

}
object GameDefinitionGen extends GameDefinitionGen
