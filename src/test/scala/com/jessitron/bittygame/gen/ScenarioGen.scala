package com.jessitron.bittygame.gen

import com.jessitron.bittygame.crux._
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalacheck.util.Pretty
import Math.min

trait ActionConditionGen {

  def conditionGen(itemsInGame: Seq[Item]) = for {
    itemToGetInTheWay <- Gen.oneOf(itemsInGame)
  } yield Has(itemToGetInTheWay)

  def conditionsGen(itemsInGame: Seq[Item]) = for {
    howMany <- Gen.choose(0,min(2, itemsInGame.length))
    some <- Gen.listOfN(howMany, conditionGen(itemsInGame))
  } yield some
}

trait OpportunityGen extends ThingThatCanHappenGen with ItemGen with ActionConditionGen with NonEmptyStringGen {

  val triggerGen: Gen[Trigger] = nonEmptyString

  val messageGen: Gen[MessageToThePlayer] = nonEmptyString

  val victoryActionGen: Gen[Opportunity] =
  for {
    trigger <- triggerGen
    message <- messageGen
  } yield Opportunity.victory(trigger, message)

  val printActionGen: Gen[Opportunity] = for {
    trigger <- triggerGen
    message <- messageGen
  } yield Opportunity.printing(trigger, message)

  def obstacleGen(itemsInGame: Seq[Item]) = for {
    sadMessage <- messageGen
    condition <- conditionGen(itemsInGame)
  } yield Obstacle(condition, sadMessage)

  // question: what happens for an empty list of items?
  def obstaclesGen(itemsInGame: Seq[Item]) = for {
    howMany <- Gen.choose(0,min(2, itemsInGame.length))
    some <- Gen.listOfN(howMany, obstacleGen(itemsInGame))
  } yield some

  val alwaysAvailableOpportunity : Gen[Opportunity] = for {
    trigger <- triggerGen
    whatHappens <- takenOpportunityGen
  } yield  Opportunity(trigger, whatHappens, Seq(), Seq())

  def opportunityGen(itemsInGame: Seq[Item]): Gen[Opportunity] = for {
    trigger <- triggerGen
    whatHappens <- takenOpportunityGen
    obstacles <- obstaclesGen(itemsInGame)
    conditions <- conditionsGen(itemsInGame)
  } yield  Opportunity(trigger, whatHappens, conditions, obstacles)

  val playerActionGen = for {
    randomItems <- someItems
    opportunity <- opportunityGen(randomItems)
  } yield opportunity

  implicit val arbitraryOpportunity: Arbitrary[Opportunity] = Arbitrary(playerActionGen)

  implicit def prettyOpportunity(pa: Opportunity):Pretty = Pretty { p =>
    s"Type ${pa.trigger} to " + prettyWhatHappens(pa.results)(p) + "\n  if " + pa.conditions + "\n  unless " + pa.obstacles
  }
}

trait ScenarioGen extends OpportunityGen with ScenarioTitleGen{

  val possibilitiesGen: Gen[Seq[Opportunity]] = Gen.resize(10,Gen.listOf(playerActionGen))

  val welcomeMessageGen: Gen[MessageToThePlayer] = nonEmptyString

  val scenarioGen = for {
    title <- scenarioTitleGen
    possibilities <- possibilitiesGen
    welcome <- welcomeMessageGen
  } yield Scenario(title, possibilities, welcome)

  implicit val arbitraryScenario: Arbitrary[Scenario] = Arbitrary(scenarioGen)

  implicit def prettyScenario
  (g: Scenario): Pretty = Pretty { p =>
    s"Scenario: \n  Welcome: ${g.welcome}\n" +
      g.possibilities.map(prettyOpportunity(_)(p)).map("  " + _).mkString("\n")
  }

}
object ScenarioGen extends ScenarioGen
