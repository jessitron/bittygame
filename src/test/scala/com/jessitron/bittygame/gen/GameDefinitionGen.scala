package com.jessitron.bittygame.gen

import com.jessitron.bittygame.crux._
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalacheck.util.Pretty

trait PlayerActionGen extends ThingThatCanHappenGen {

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

  implicit val playerActionArb: Arbitrary[PlayerAction] = Arbitrary(playerActionGen)

  implicit def prettyPlayerAction(pa: PlayerAction):Pretty = Pretty { p =>
    s"Type ${pa.trigger} to " + prettyWhatHappens(pa.results)(p)
  }
}

trait GameDefinitionGen extends PlayerActionGen {

  val possibilitiesGen: Gen[Seq[PlayerAction]] = Gen.listOf(playerActionGen)

  val welcomeMessageGen: Gen[MessageToThePlayer] = Gen.alphaStr.suchThat(_.nonEmpty)

  val gameDefGen = for {
    possibilities <- possibilitiesGen
    welcome <- welcomeMessageGen
  } yield GameDefinition(possibilities, welcome)

  implicit val arbitraryGameDef: Arbitrary[GameDefinition] = Arbitrary(gameDefGen)

  implicit def prettyGameDef(g: GameDefinition): Pretty = Pretty { p =>
    s"GameDefinition: \n${g.welcome}\n" +
      g.possibilities.map(prettyPlayerAction(_)(p)).map("  " + _).mkString("\n")
  }

}
object GameDefinitionGen extends GameDefinitionGen
