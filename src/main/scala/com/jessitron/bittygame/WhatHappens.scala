package com.jessitron.bittygame

sealed trait ThingThatCanHappen
case class Print(message: String) extends ThingThatCanHappen
case object ExitGame extends ThingThatCanHappen

case class WhatHappens(results: Seq[ThingThatCanHappen])

object WhatHappens {
  val NothingHappens = WhatHappens(Seq())
}
