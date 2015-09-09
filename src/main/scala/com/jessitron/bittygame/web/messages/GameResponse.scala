package com.jessitron.bittygame.web.messages

import com.jessitron.bittygame.crux.{GameState, ThingThatCanHappen, WhatHappens}
import com.jessitron.bittygame.serialization._
import spray.json.DefaultJsonProtocol._

/*
 * this is used only in the web, to group these two things.
 */
case class GameResponse(state: GameState, instructions: Seq[ThingThatCanHappen])
object GameResponse {
  def apply(tuple: (GameState, WhatHappens)): GameResponse = GameResponse(tuple._1, tuple._2.results)
  implicit val format = jsonFormat2(apply)
}
