package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux.Scenario
import com.jessitron.bittygame.web.identifiers.ScenarioKey
import org.scalacheck.Gen
import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import com.jessitron.bittygame.gen._

class FullGameProperties
  extends org.scalatest.PropSpec
  with GeneratorDrivenPropertyChecks
  with ShouldMatchers
  with BittyGameServiceTestiness {

  val scenarioAndName = for {
    scenarioName <- gameNameGen
    scenario <- scenarioGen
  } yield (scenarioName, scenario)

  val SCENARIOS_TO_GENERATE = 10
  val severalScenarios : Seq[(ScenarioKey, Scenario)] =
    Iterator.continually(scenarioAndName.sample).collect{ case Some(a) => a}.take(SCENARIOS_TO_GENERATE).toSeq

  severalScenarios.foreach { case (key, scenario) => scenarioDAO.save(key, scenario)}

  val storedScenarioKey: Gen[ScenarioKey] = Gen.oneOf(severalScenarios.map(_._1))

  property("Anything returned by Think, it knows how to do") {
    forAll(storedScenarioKey) {


    }

  }

}
