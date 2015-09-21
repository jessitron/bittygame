package com.jessitron.bittygame

import com.jessitron.bittygame.crux.{Item, Scenario}
import com.jessitron.bittygame.gen.ScenarioGen
import org.scalacheck.{Gen, Shrink}

// this just refuses to compile in a worksheet. Sigh.


object WhatIsAShrinker extends ScenarioGen {


  def untilYouGetOne[T](how: => Option[T], limit: Int = 1000): T =
    Iterator.continually(how)
      .take(limit)
      .collectFirst{ case Some(x) => x}
      .getOrElse(throw new RuntimeException(s"Didn't get one after $limit tries"))


  def main(args: Array[String]) {

    val stringShrink = implicitly[Shrink[String]]
    val smallerStrings = stringShrink.shrink("yes")

    println("shrink a string:")
    smallerStrings.foreach(a => println("<" + a + ">"))

    printTheShrinks(scenarioGen, "scenario")(scenarioShrink)

    printTheShrinks(scenarioTitleGen, "title")(nonEmptyShrinker)

    printTheShrinks(oneOpportunityGen, "one opportunity")(oneOpportunityShrink)

  }

  def printTheShrinks[T](g: Gen[T], name: String)(implicit shrinker: Shrink[T]): Unit = {
    val r = untilYouGetOne(g.sample)
    println(s"------ $name ------\n")
    println("Generated: " + r)
    println("Shrinks: ")
    shrinker.shrink(r).foreach(a => println("<" + a + ">"))
  }

}
