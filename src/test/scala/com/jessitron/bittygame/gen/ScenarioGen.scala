package com.jessitron.bittygame.gen

import com.jessitron.bittygame.crux._
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalacheck.util.Pretty

trait OpportunityGen extends ThingThatCanHappenGen with ItemGen {

  val triggerGen: Gen[Trigger] = Gen.alphaStr.suchThat(_.nonEmpty)

  val messageGen: Gen[MessageToThePlayer] = Gen.alphaStr.suchThat(_.nonEmpty)

  val victoryActionGen: Gen[Opportunity] =
  for {
    trigger <- triggerGen
    message <- messageGen
  } yield Opportunity.victory(trigger, message)

  val printActionGen: Gen[Opportunity] = for {
    trigger <- triggerGen
    message <- messageGen
  } yield Opportunity.printing(trigger, message)

  val playerActionGen = Gen.frequency((4,printActionGen), (1,victoryActionGen))

  implicit val arbitratyOpportunity: Arbitrary[Opportunity] = Arbitrary(playerActionGen)

  implicit def prettyOpportunity(pa: Opportunity):Pretty = Pretty { p =>
    s"Type ${pa.trigger} to " + prettyWhatHappens(pa.results)(p)
  }
}

trait ScenarioGen extends OpportunityGen {

  val possibilitiesGen: Gen[Seq[Opportunity]] = Gen.listOf(playerActionGen)

  val welcomeMessageGen: Gen[MessageToThePlayer] = Gen.alphaStr.suchThat(_.nonEmpty)

  val scenarioGen = for {
    possibilities <- possibilitiesGen
    welcome <- welcomeMessageGen
  } yield Scenario(possibilities, welcome)

  implicit val arbitraryScenario: Arbitrary[Scenario] = Arbitrary(scenarioGen)

  implicit def prettyScenario
  (g: Scenario): Pretty = Pretty { p =>
    s"Scenario: \n  Welcome: ${g.welcome}\n" +
      g.possibilities.map(prettyOpportunity(_)(p)).map("  " + _).mkString("\n")
  }

}
object ScenarioGen extends ScenarioGen
