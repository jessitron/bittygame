package com.jessitron.bittygame.crux


sealed trait ThingThatCanHappen
case class Print(message: MessageToThePlayer) extends ThingThatCanHappen {
  assert(message.nonEmpty, "An empty message is worse than no message at all" )
}
case object ExitGame extends ThingThatCanHappen

case class WhatHappens(results: Seq[ThingThatCanHappen]) {
  def and(nextThing: ThingThatCanHappen) = this.copy(results = results :+ nextThing)
}

object WhatHappens {
  val NothingHappens = WhatHappens(Seq())

  def thisHappens(one: ThingThatCanHappen): WhatHappens = WhatHappens(Seq(one))
}
