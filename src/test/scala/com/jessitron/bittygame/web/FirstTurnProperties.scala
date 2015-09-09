package com.jessitron.bittygame.web

import com.jessitron.bittygame.web.ports.GameDefinitionDAO.GameDefinitionKey
import org.scalatest.{PropSpec, Assertions}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import com.jessitron.bittygame.gen._
import spray.http.StatusCodes

class FirstTurnProperties
  extends PropSpec
  with GeneratorDrivenPropertyChecks
  with BittyGameServiceTestiness
  with Assertions {

  property("unknown game returns 404") {
    forAll(gameNameGen) { nonexistentGame: GameDefinitionKey =>
      whenever(!gameDefinitions.names().contains(nonexistentGame)) {
        Get(s"/game/$nonexistentGame/begin") ~> myRoute ~> check {
          assert(status === StatusCodes.NotFound)
        }
      }
    }
  }

  /* I was running 'begin $(random)' in the command line,
   * and I got a 404. But it didn't print what game it hit so
   * I don't know what input caused that.
   * Perfect for a property test!
   */
  property("any game created by random, I can take a first turn") {
    
  }
}
