package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux._
import com.jessitron.bittygame.gen.{ScenarioTitleGen, GameStateGen}
import com.jessitron.bittygame.web.messages.GameResponse
import com.jessitron.bittygame.web.ports.ScenarioDAO
import org.scalacheck.{Test, Prop, Gen}
import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalacheck.Prop.BooleanOperators

trait FullGameGen extends GameStateGen with ScenarioTitleGen {

  val scenarioDAO : ScenarioDAO

  val SCENARIOS_TO_GENERATE = 10
  val severalScenarios : Seq[Scenario] =
    Iterator.continually(scenarioGen.sample).collect{ case Some(a) => a}.take(SCENARIOS_TO_GENERATE).toSeq

  /*** STORE THEM ***/
  severalScenarios.foreach { case scenario => scenarioDAO.save(scenario.title, scenario)}

  val storedScenario: Gen[Scenario] = Gen.oneOf(severalScenarios)

  def generatedScenario(key: ScenarioTitle) = severalScenarios.find{ _.title == key}.getOrElse(throw new RuntimeException("Where did it go?"))

}

class FullGameProperties
  extends org.scalatest.PropSpec
  with GeneratorDrivenPropertyChecks
  with ShouldMatchers
  with BittyGameServiceTestiness
  with FullGameGen {

  val whatINeed: Gen[(Scenario, Seq[String], Seq[String])] = for {
    scenario <- storedScenario
    validMoves = scenario.possibilities.map(_.trigger) // TODO: that don't exit the game
    someValidMoves <- Gen.listOf(Gen.oneOf(validMoves))
    someInvalidMoves <- Gen.listOfN(2, triggerGen.suchThat(!someValidMoves.contains(_)))
  } yield (scenario, someValidMoves, someInvalidMoves)

  property("Anything returned by Think, it knows how to do") {
    forAll(whatINeed) { input =>
      val (scenario, someValidMoves, someInvalidMoves) = input
      val title = scenario.title
      val moves = scala.util.Random.shuffle(someValidMoves ++ someInvalidMoves)

      /* Step 1: take the first turn */
      val gameID = callBeginGame(title).gameID

      /* Step 2: take more turns, get into some random place */
      moves.foreach(callTakeTurn(gameID, _))

      /* Step 3: perform the test */
      val thoughts = callThink(gameID)

      /* Step 4: check the state of the output data in the world */
      
      def isRecognized(thought: String) : Prop = isRecognizedResponse(callTakeTurn(gameID, thought)) :| s"Not recognized: <$thought>"
      val canDoAllTheThingsWeCanThink: Prop =
        Prop.all(
          thoughts.map(isRecognized)
          : _*)

      val unthought =
        allTriggers(scenario).                 // everything in the game
          filterNot(thoughts.contains(_))      // that we didn't think of

      def notRecognized(thought: String) : Prop = isUnrecognizedResponse(callTakeTurn(gameID, thought)) :| s"Oops, recognized: <$thought>"
      val cannotDoThingsWeDidntThinkOf =
        Prop.all(
          unthought.map(notRecognized)
            : _*)

      // using ScalaCheck to accumulate ACTUAL DATA about the failure. Could have used Scalatest 'withClue' instead
      val propertyResult =
        Test.check(Test.Parameters.default,
          canDoAllTheThingsWeCanThink && cannotDoThingsWeDidntThinkOf)

      /* Step 5: give Scalatest its exception if the properties don't hold */
      assert(propertyResult.passed, s"Failure: ${labels(propertyResult.status)}\n ${printScenario(scenario)}")

    }

  }

  private def allTriggers(scenario: Scenario): Seq[String] =
    scenario.possibilities.map(_.trigger)

  private def iDontKnowHow(thing: ThingThatCanHappen): Boolean =
    thing match {
      case IDontKnowHowTo(_) => true
      case _ => false
    }


  private def labels(status: Test.Status): Set[String] =
    status match {
      case Test.Failed(_, labels) => labels
      case _ => Set()
    }

  private def isRecognizedResponse(response: GameResponse): Prop = {
    val happenings = response.instructions
    (happenings.nonEmpty :| "something should happen") &&
      (!happenings.exists(iDontKnowHow) :| "don't say I don't know how to")
  }

  private def isUnrecognizedResponse(response: GameResponse) : Prop = {
    response.instructions.exists(iDontKnowHow) :| "should say I don't know how"
  }


}
