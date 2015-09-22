package com.jessitron.bittygame.web.messages

import com.jessitron.bittygame.crux.{TurnResult}
import com.jessitron.bittygame.serialization._
import com.jessitron.bittygame.web.identifiers.GameID
import spray.json.DefaultJsonProtocol._

/*
 * this is used only in the web, to group these two things.
 */
case class GameResponse(gameID: GameID, instructions: Seq[TurnResult])
object GameResponse {
  implicit val format = jsonFormat2(apply)
}
