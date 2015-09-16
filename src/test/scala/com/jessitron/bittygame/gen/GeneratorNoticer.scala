package com.jessitron.bittygame.gen

import org.scalacheck.Gen

object GeneratorNoticer extends GameStateGen {

  def main(args: Array[String]) {
    printAbout("nonemptystring", nonEmptyString, 1000)
    printAbout("item", itemGen, 1000)
    printAbout("list of items", someItems)
    printAbout("playerAction", playerActionGen, 1000)
    printAbout("possibilities", possibilitiesGen)
    printAbout("welcome", welcomeMessageGen, 1000)
    printAbout("scenario", scenarioGen)
    printAbout("scenarioAndState", scenarioAndStateGen)
  }

  def printAbout[T](name: String, gen: Gen[T], n: Int = 100): Unit = {
    println(s"${countPopulated(gen, n)}% failure in $name")
  }

  def countPopulated[T](g: Gen[T], n: Int) = {
    val noValue = Iterator.continually(g.sample).take(n).collect{ case None => 1 }.length

    noValue * 100 / n
  }

}
