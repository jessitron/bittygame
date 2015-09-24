package com.jessitron.bittygame.gen

import com.jessitron.bittygame.crux._
import org.scalacheck.{Shrink, Arbitrary, Gen}
import org.scalacheck.util.Pretty
import Math.min

trait ConditionGen {

  def itemInTheWayGen(itemsInGame: Seq[Item]) = for {
    itemToGetInTheWay <- Gen.oneOf(itemsInGame)
  } yield Has(itemToGetInTheWay)

  def mustHaveAStatGen(stat: Stat) =
    for {
      level <- Gen.choose(stat.low + 1, stat.high)
    } yield StatAtLeast(stat.name, level)

  def bigMustHaveGen(stats:Seq[Stat]) =
    for{
      stat <- Gen.oneOf(stats)
      gen <- mustHaveAStatGen(stat)
    } yield gen

  def conditionGen(itemsInGame: Seq[Item], statsInGame: Seq[Stat]) : Gen[Condition] ={
    val gens:Seq[Gen[Condition]] = (if(itemsInGame.nonEmpty) Seq(itemInTheWayGen(itemsInGame)) else Nil) ++
                (if(statsInGame.nonEmpty) Seq(bigMustHaveGen(statsInGame)) else Nil)
    if(gens.isEmpty) Gen.fail else
    if(gens.size == 1) gens.head else
    if(gens.size == 2) Gen.oneOf(gens.head,gens.tail.head) else
     Gen.oneOf(gens.head,gens.tail.head,gens.tail.tail :_*)
  }

  import scala.collection.JavaConversions._ // OMFG
  def conditionsGen(itemsInGame: Seq[Item], statsInGame: Seq[Stat]) : Gen[Seq[Condition]] = for {
    itemLimits <- Gen.someOf(itemsInGame.map(Has(_)))
    statThingers: Seq[StatAtLeast] <- Gen.sequence(statsInGame.map(mustHaveAStatGen)).map(_.toSeq)
    statHighEnough <- Gen.someOf(statThingers)
  } yield itemLimits ++ statHighEnough
}

trait OpportunityGen extends ThingThatCanHappenGen with ItemGen with ConditionGen with NonEmptyStringGen {

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

  def obstacleGen(itemsInGame: Seq[Item], statsInGame: Seq[Stat]) = for {
    sadMessage <- messageGen
    condition <- conditionGen(itemsInGame, statsInGame)
  } yield Obstacle(condition, sadMessage)

  // question: what happens for an empty list of items?
  def obstaclesGen(itemsInGame: Seq[Item],statsInGame: Seq[Stat]) = for {
    howMany <- Gen.choose(0,min(2, itemsInGame.length))
    some <- Gen.listOfN(howMany, obstacleGen(itemsInGame,statsInGame))
  } yield some

  def alwaysAvailableOpportunity(statsInGame: Seq[Stat]) : Gen[Opportunity] = for {
    trigger <- triggerGen
    items <- someItemsGen
    whatHappens <- thingsThatHappenWhenYouTakeAnOpportunityGen(items, statsInGame)
  } yield  Opportunity(trigger, whatHappens, Seq(), Seq())

  def opportunityGen(itemsInGame: Seq[Item], statsInGame: Seq[Stat]): Gen[Opportunity] =
    for {
      trigger <- triggerGen
      whatHappens <- thingsThatHappenWhenYouTakeAnOpportunityGen(itemsInGame, statsInGame)
      obstacles <- obstaclesGen(itemsInGame,statsInGame)
      conditions <- conditionsGen(itemsInGame,statsInGame)
    } yield  Opportunity(trigger, whatHappens, conditions, obstacles)

  val oneOpportunityGen = for {
    randomItems <- someItemsGen
    randomStats <- someStatsGen
    opportunity <- opportunityGen(randomItems, randomStats)
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

trait ScenarioGen extends OpportunityGen with ScenarioTitleGen with StatGen with GameStateGen {

  val welcomeMessageGen: Gen[MessageToThePlayer] = nonEmptyString

  def soMany[T](low: Int, high: Int, what: Gen[T]) : Gen[Seq[T]] =
    for {
      howMany <- Gen.choose(low, high)
      hereTheyAre <- Gen.listOfN(howMany, what)
    } yield hereTheyAre

  val scenarioGen: Gen[Scenario] = for {
    title <- scenarioTitleGen
    welcome <- welcomeMessageGen
    stats <- someStatsGen
    items <- someItemsGen
    opportunities <- soMany(2, 12, opportunityGen(items,stats))
  } yield Scenario(title, opportunities, welcome, stats, items)


  def noConflict(scenario: Scenario, opportunity: Opportunity): Boolean =
    !scenario.opportunities.exists(_.conflictsWith(opportunity))


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

  def gameStateFor(scenario: Scenario) = gameStateGen(scenario.title, scenario.items, scenario.stats)

  def scenarioAndStateGen: Gen[(Scenario, GameState)] =
    for {
      scenario <- scenarioGen
      gameState <- gameStateFor(scenario)
    } yield (scenario, gameState)

  implicit val arbScenarioAndState: Arbitrary[(Scenario, GameState)] = Arbitrary(scenarioAndStateGen)

  implicit def prettyGameAndState(gameAndState: (Scenario, GameState)): Pretty =
    Pretty { p =>
      val (scenario, gameState) = gameAndState
      val scenarioPretty = implicitly[Scenario => Pretty]
      s"GameState: $gameState\n" + scenarioPretty(scenario)(p)
    }

}
object ScenarioGen extends ScenarioGen
