package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux.ScenarioTitle
import com.jessitron.bittygame.gen.ScenarioTitleGen
import com.jessitron.bittygame.web.messages.CreateRandomScenarioResponse
import org.scalatest.{PropSpec, Assertions}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import spray.httpx.SprayJsonSupport._
import com.jessitron.bittygame.serialization._
import spray.http.StatusCodes

class FirstTurnProperties
  extends PropSpec
  with GeneratorDrivenPropertyChecks
  with BittyGameServiceTestiness
  with Assertions
  with ScenarioTitleGen {

  property("unknown game returns 404") {
    forAll(scenarioTitleGen) { nonexistentGame: ScenarioTitle =>
      whenever(!scenarioDAO.names().contains(nonexistentGame)) {
        Get(s"/scenario/$nonexistentGame/begin") ~> myRoute ~> check {
          assert(status === StatusCodes.NotFound)
        }
      }
    }
  }

  /* I was running 'begin $(random)' in the command line,
   * and I got a 404. But it didn't print what game it hit so
   * I don't know what input caused that.
   * Perfect for a property test!
   *
   * (it turned out to be spaces, and I added URL encoding)
   */
  property("any game created by random, I can take a first turn") {
    forAll { i: Int => // it doesn't really matter what it is
      Get("/random") ~> myRoute ~> check {
        val goFirst = responseAs[CreateRandomScenarioResponse].first_turn_url
        Get(goFirst) ~> myRoute ~> check {
          assert(status === StatusCodes.OK, s"Game name was $goFirst")
        }
      }
    }
  }
}
