package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux.{GameState, WhatHappens}
import spray.json.DefaultJsonProtocol._
import com.jessitron.bittygame.serialization._

/*
 * this is used only in the web, to group these two things.
 */
case class GameResponse(state: GameState, instructions: WhatHappens)
object GameResponse {
  implicit val format = jsonFormat2(apply)
}
