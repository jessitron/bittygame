package com.jessitron.bittygame.gen

import org.scalacheck.Gen

trait ScenarioTitleGen extends NonEmptyStringGen {

  val scenarioTitleGen: Gen[String] = nonEmptyString

}
