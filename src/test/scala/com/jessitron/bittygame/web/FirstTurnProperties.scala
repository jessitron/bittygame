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
}
