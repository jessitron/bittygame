package com.jessitron.bittygame.web.messages

import com.jessitron.bittygame.web.identifiers.GameID
import spray.json.DefaultJsonProtocol._

case class GameTurn(playerMove: String, gameID: GameID)
object GameTurn {
  implicit val format = jsonFormat2(apply)
}
