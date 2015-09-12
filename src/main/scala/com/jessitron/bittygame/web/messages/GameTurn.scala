package com.jessitron.bittygame.web.messages

import com.jessitron.bittygame.crux.GameState
import spray.json.DefaultJsonProtocol._

case class GameTurn(typed: String, state: GameState)
object GameTurn {
  implicit val format = jsonFormat2(apply)
}
