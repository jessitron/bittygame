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
  
  property("Anything returned by Think, it knows how to do") {
    forAll(whatINeed) { case (scenario, someValidMoves, someInvalidMoves) =>
      val title = scenario.title
      val moves = scala.util.Random.shuffle(someValidMoves ++ someInvalidMoves)

      /* Step 0: store the scenario */
      create(scenario)

      /* Step 1: take the first turn */
      val gameID = callBeginGame(title).gameID

      /* Step 2: take more turns, get into some random place */
      moves.foreach(callTakeTurn(gameID, _))

      /* Step 3: perform the test */
      val thoughts = callThink(gameID)

      /* Step 4: check all the things I can't do */

      val unthought =
        allTriggers(scenario).                 // everything in the game
          filterNot(thoughts.contains(_))      // that we didn't think of

      unthought.foreach { notThought =>
        withClue(s"We should not recognize $notThought") {
          val response = callTakeTurn(gameID, notThought)
          assert(response.instructions.exists(iDontKnowHow), s"should say I don't know how. Got: $response")
        }
      }

      /* Step 5: check one of the things I can do. I can only check one, because doing so changes the state */
      whenever (thoughts.nonEmpty) {
        val oneThought = scala.util.Random.shuffle(thoughts).head
        withClue("I think I will: $oneThought") {
          val response = callTakeTurn(gameID, oneThought)

          val happenings = response.instructions
          assert(happenings.nonEmpty, "something should happen")
          assert(!happenings.exists(iDontKnowHow),s"don't say I don't know how to. Said: $happenings")
        }
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

  // TODO: we never get what the client can't handle

}
