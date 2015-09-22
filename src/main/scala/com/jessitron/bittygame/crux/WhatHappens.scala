package com.jessitron.bittygame.crux


sealed trait TurnResult
case class Print(message: MessageToThePlayer) extends TurnResult {
  assert(message.nonEmpty, "An empty message is worse than no message at all" )
}
case object ExitGame extends TurnResult
case object Win extends TurnResult
case class Acquire(item: Item) extends TurnResult
case class IDontKnowHowTo(what: String) extends TurnResult
case class CantDoThat(why: MessageToThePlayer) extends TurnResult
case class IncreaseStat(which: StatID) extends TurnResult

case class WhatHappens(results: Seq[TurnResult]) {
  def andMaybe(mightHappen: Option[TurnResult]): WhatHappens =
    mightHappen match {
      case None => this
      case Some(thing) => this.and(thing)
    }

  def and(nextThing: TurnResult) = this.copy(results = results :+ nextThing)

  def tellTheClient: Seq[TurnResult] = results
}

object WhatHappens {
  val NothingHappens = WhatHappens(Seq())

  def thisHappens(one: TurnResult): WhatHappens = WhatHappens(Seq(one))
}
