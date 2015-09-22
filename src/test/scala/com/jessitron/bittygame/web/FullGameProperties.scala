package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux._
import com.jessitron.bittygame.gen.{ScenarioGen}
import com.jessitron.bittygame.web.messages.GameResponse
import com.jessitron.bittygame.web.ports.ScenarioDAO
import org.scalacheck.{Shrink, Test, Prop, Gen}
import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalacheck.Prop.BooleanOperators


class FullGameProperties
  extends org.scalatest.PropSpec
  with GeneratorDrivenPropertyChecks
  with ShouldMatchers
  with BittyGameServiceInfrastructure
  with ScenarioGen {

  val whatINeed: Gen[(Scenario, Seq[String], Seq[String])] = for {
    scenario <- scenarioGen
    validMoves = scenario.opportunities.map(_.trigger) // TODO: that don't exit the game
    someValidMoves <- Gen.listOf(Gen.oneOf(validMoves))
    someInvalidMoves <- Gen.listOfN(2, triggerGen.suchThat(!someValidMoves.contains(_)))
  } yield (scenario, someValidMoves, someInvalidMoves)

//  implicit val ownSpecialShrinker: Shrink[(Scenario, Seq[String], Seq[String])] = Shrink {
//    case(scenario, moves, moreMoves) =>
//      val fewerValidMoves = if (moves.nonEmpty) Some(scenario, moves.dropRight(1), moreMoves) else None
//      val fewerOtherMoves = if (moreMoves.nonEmpty) Some(scenario, moves, moreMoves.dropRight(1)) else None
//
//      val tryThese = fewerValidMoves.toSeq ++ fewerOtherMoves.toSeq
//      Stream(tryThese :_*)
//  }

  property("Anything returned by Think, it knows how to do") {
    forAll(whatINeed) { case (scenario, someValidMoves, someInvalidMoves) =>
      val title = scenario.title
      val moves = (someValidMoves ++ someInvalidMoves)

      /* Step 0: store the scenario */
      create(scenario)

      /* Step 1: take the first turn */
      val gameID = callBeginGame(title).gameID

      /* Step 2: take more turns, get into some random place */
      moves.foreach(callTakeTurn(gameID, _))

      /* Step 3: perform the test */
      val thoughts = callThink(gameID)

      /* Step 4: check the state of the output data in the world */

      val unthought =
        allTriggers(scenario).                 // everything in the game
          filterNot(thoughts.contains(_))      // that we didn't think of

      def notRecognized(thought: String) : Prop = isUnrecognizedResponse(callTakeTurn(gameID, thought)) :| s"Oops, recognized: <$thought>"
      val cannotDoThingsWeDidntThinkOf =
        Prop.all(
          unthought.map(notRecognized)
            : _*)

      def isRecognized(thought: String) : Prop = isRecognizedResponse(callTakeTurn(gameID, thought)) :| s"Not recognized: <$thought>"
      val canDoAllTheThingsWeCanThink: Prop =
        Prop.all(
          thoughts.take(1).map(isRecognized)
            : _*)

      // using ScalaCheck to accumulate ACTUAL DATA about the failure. Could have used Scalatest 'withClue' instead
      val propertyResult =
        Test.check(Test.Parameters.default,
          canDoAllTheThingsWeCanThink && cannotDoThingsWeDidntThinkOf)

      /* Step 5: give Scalatest its exception if the properties don't hold */
      withClue(s"Failure: ${labels(propertyResult.status)}\n ${printScenario(scenario)}\n Thought of: $thoughts") {
        propertyResult.passed should be(true)
      }

    }

  }

  private def allTriggers(scenario: Scenario): Seq[String] =
    scenario.opportunities.map(_.trigger)

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

  // TODO: we never get what the client can't handle

}
