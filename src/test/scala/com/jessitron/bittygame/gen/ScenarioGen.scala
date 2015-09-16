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

  def thisOften(percentage: Int): Gen[Boolean] = Gen.frequency(((100 - percentage), false), (percentage, true))

  def maybeDo(so: Boolean, doThis: Opportunity => Opportunity)(to: Opportunity): Opportunity = {
    if (so)
      doThis(to)
    else
      to
  }

  def opportunityGen(itemsInGame: Seq[Item]): Gen[Opportunity] = for {
    trigger <- triggerGen
    message <- messageGen
    itemToGet <- Gen.oneOf(itemsInGame)
    itemToRequire <- Gen.oneOf(itemsInGame)
    itemToGetInTheWay <- Gen.oneOf(itemsInGame)
    sadMessage <- messageGen
    acquireSomething <- Gen.oneOf(true, false)
    requireSomething <- Gen.oneOf(true, false)
    win <- thisOften(10)
    exit <- thisOften(20)
    obstacle <- thisOften(25)
  } yield {
      val opportunity = Opportunity.printing(trigger, message)
      val doTheseSometimes: Seq[Opportunity => Opportunity] = scala.util.Random.shuffle(
      List (
        maybeDo(acquireSomething, _.andProvides(itemToGet)),
        maybeDo(requireSomething, _.onlyIf(Has(itemToRequire))),
        maybeDo(win, _.andWin),
        maybeDo(exit, _.andExit),
        maybeDo(obstacle, _.behindObstacle(Has(itemToGetInTheWay), sadMessage))
      ))

      doTheseSometimes.foldLeft(opportunity){case (o, f) => f(o)}
    }

  val playerActionGen = for {
    randomItems <- Gen.listOf(itemGen)
    opportunity <- opportunityGen(randomItems)
  } yield opportunity

  implicit val arbitraryOpportunity: Arbitrary[Opportunity] = Arbitrary(playerActionGen)

  implicit def prettyOpportunity(pa: Opportunity):Pretty = Pretty { p =>
    s"Type ${pa.trigger} to " + prettyWhatHappens(pa.results)(p) + "\n  if " + pa.conditions + "\n  unless " + pa.obstacles
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
