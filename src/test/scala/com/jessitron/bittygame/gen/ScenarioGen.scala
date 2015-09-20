package com.jessitron.bittygame.gen

import com.jessitron.bittygame.crux._
import org.scalacheck.{Shrink, Arbitrary, Gen}
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
    whatHappens <- someItems.flatMap(thingsThatHappenWhenYouTakeAnOpportunityGen(_))
  } yield  Opportunity(trigger, whatHappens, Seq(), Seq())

  def opportunityGen(itemsInGame: Seq[Item]): Gen[Opportunity] =
    for {
      trigger <- triggerGen
      whatHappens <- thingsThatHappenWhenYouTakeAnOpportunityGen(itemsInGame)
      obstacles <- obstaclesGen(itemsInGame)
      conditions <- conditionsGen(itemsInGame)
    } yield  Opportunity(trigger, whatHappens, conditions, obstacles)

  val oneOpportunityGen = for {
    randomItems <- someItems
    opportunity <- opportunityGen(randomItems)
  } yield opportunity

  implicit val oneOpportunityShrink: Shrink[Opportunity] = Shrink {
    orig: Opportunity =>
      val fewerObstacles = Shrink.shrink(orig.obstacles).map(x => orig.copy(obstacles = x))
      val fewerConditions = Shrink.shrink(orig.conditions).map(x => orig.copy(conditions = x))
      val fewerHappenings = Shrink.shrink(orig.results).map(x => orig.copy(results = x))
      val shorterTrigger = nonEmptyShrinker.shrink(orig.trigger).map(x => orig.copy(trigger = x))

      fewerObstacles ++ fewerConditions ++ fewerHappenings ++ shorterTrigger
  }

  implicit val arbitraryOpportunity: Arbitrary[Opportunity] = Arbitrary(oneOpportunityGen)

  def printOpportunity(pa: Opportunity) =    s"Type ${pa.trigger} to " + printWhatHappens(pa.results) + "\n  if " + pa.conditions + "\n  unless " + pa.obstacles


  implicit def prettyOpportunity(pa: Opportunity):Pretty = Pretty { p =>
    printOpportunity(pa)
  }
}

trait StatGen extends NonEmptyStringGen {

  val minStatLow = -10
  val maxStatHigh = 10

  val statGen : Gen[Stat] =
    for {
      name <- nonEmptyString
      low <- Gen.choose(minStatLow, maxStatHigh)
      high <- Gen.choose(low, maxStatHigh)
      start <- Gen.choose(low, high)
    } yield Stat(name, low, high, start)

  val someStatsGen: Gen[Seq[Stat]] =
  for {
    n <- Gen.choose(0,4)
    stats <- Gen.listOfN(n, statGen)
  } yield stats

  import scala.collection.JavaConversions._ // why on Earth does Gen.sequence use java.util.ArrayList? sigh
  /* choose cromulent starting values for a slew of stats */
  def startingValuesGen(stats: Seq[Stat]) : Gen[Seq[(StatID, Int)]] =
    Gen.sequence (stats.map { stat =>
      Gen.choose(stat.low, stat.high).map ((stat.name, _))
    }).map(_.toSeq)

}

trait ScenarioGen extends OpportunityGen with ScenarioTitleGen with StatGen {

  val opportunitiesGen: Gen[Seq[Opportunity]] = Gen.resize(10,Gen.listOf(oneOpportunityGen))

  val welcomeMessageGen: Gen[MessageToThePlayer] = nonEmptyString

  val scenarioGen: Gen[Scenario] = for {
    title <- scenarioTitleGen
    possibilities <- opportunitiesGen
    welcome <- welcomeMessageGen
    stats <- someStatsGen
  } yield Scenario(title, possibilities, welcome, stats)

  implicit val scenarioShrink =
    Shrink{ s: Scenario =>
      val opportunityShrinks = Shrink.shrink(s.opportunities)
      opportunityShrinks.map(ops => s.copy(opportunities = ops))
    }

  implicit val arbitraryScenario: Arbitrary[Scenario] = Arbitrary(scenarioGen)

  def printScenario(g: Scenario) =
    s"Scenario: \n  Welcome: ${g.welcome}\n" +
    g.opportunities.map(printOpportunity).map("  " + _).mkString("\n") +
    s"\n  Stats: ${g.stats}"

  implicit def prettyScenario
  (g: Scenario): Pretty = Pretty { p =>
    printScenario(g)
  }

}
object ScenarioGen extends ScenarioGen
