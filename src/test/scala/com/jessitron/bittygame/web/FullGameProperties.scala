package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux._
import com.jessitron.bittygame.gen.{ScenarioGen}
import com.jessitron.bittygame.web.identifiers.GameID
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

  property("Anything returned by Think, it knows how to do") {
    forAll(MiddleOfGame.gen) { middleOfGame =>

      /* Set up */
      val gameID = middleOfGame.goThere(this)

      /* Test */
      val thoughts = callThink(gameID)

      /* Check */
      whenever (thoughts.nonEmpty) {
        val oneThought = scala.util.Random.shuffle(thoughts).head
        assertRecognized(gameID, oneThought)
      }
    }
  }

  property("Anything not returned by Think, it doesn't know how to do") {
    forAll(MiddleOfGame.gen) { middleOfGame =>

      /* Set up */
      val gameID = middleOfGame.goThere(this)

      /* Perform the test */
      val thoughts = callThink(gameID)

      /* Check */
      val unthought = middleOfGame.
          allMovesThatMightEverBePossible.     // everything in the game
          filterNot(thoughts.contains(_))      // that we didn't think of

      assertMovesNotRecognized(gameID, unthought)

    }
  }

  def assertRecognized(gameID: GameID, oneThought: String): Unit = {
    withClue(s"I think I will: $oneThought") {
      val response = callTakeTurn(gameID, oneThought)

      val happenings = response.instructions
      assert(happenings.nonEmpty, "something should happen")
      assert(!happenings.exists(iDontKnowHow), s"don't say I don't know how to. Said: $happenings")
    }
  }

  def assertMovesNotRecognized(gameID: GameID, unthought: Seq[Trigger]): Unit = {
    unthought.foreach { notThought =>
      withClue(s"We should not recognize $notThought") {
        val response = callTakeTurn(gameID, notThought)
        assert(response.instructions.exists(iDontKnowHow), s"should say I don't know how. Got: $response")
      }
    }
  }

  private def iDontKnowHow(thing: TurnResult): Boolean =
    thing match {
      case IDontKnowHowTo(_) => true
      case _ => false
    }

  // TODO: we never get what the client can't handle

}
