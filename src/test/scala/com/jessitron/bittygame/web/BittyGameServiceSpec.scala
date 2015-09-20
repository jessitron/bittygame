package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux._
import com.jessitron.bittygame.gen.ScenarioGen
import com.jessitron.bittygame.web.messages.{CreateRandomScenarioResponse, GameResponse}
import org.scalatest.ShouldMatchers
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import spray.http._
import spray.httpx.SprayJsonSupport._
import com.jessitron.bittygame.serialization._
import StatusCodes._

class BittyGameServiceSpec extends org.scalatest.PropSpec
                       with GeneratorDrivenPropertyChecks
                       with ShouldMatchers
                       with BittyGameServiceInfrastructure
                       with ScenarioGen {

  property ("the first turn prints the welcome message") {
    forAll(opportunitiesGen,
           welcomeMessageGen,
           scenarioTitleGen)
    { (someActions: Seq[Opportunity],
       message: MessageToThePlayer,
        title: ScenarioTitle) =>

      whenever (title.nonEmpty && message.nonEmpty) {

        val someGame = Scenario(title, someActions, message)

        Put("/scenario/", someGame) ~> myRoute ~> check {
          status should be(Created)
        }

        Get("/scenario/" + encode(title) + "/begin") ~> myRoute ~> check {
          responseAs[GameResponse].instructions should contain(Print(message))
        }

      }
    }
  }

  property("unknown game returns 404") {
    forAll(scenarioTitleGen) { nonexistentGame: ScenarioTitle =>
      whenever(!scenarioDAO.names().contains(nonexistentGame)) {
        callToTheFirstGameEndpoint(nonexistentGame) ~> myRoute ~> check {
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
