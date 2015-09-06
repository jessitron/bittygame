package com.jessitron.bittygame.crux

import spray.json.DefaultJsonProtocol._

case class PlayerAction(trigger: String, printedResponse: MessageToThePlayer)
object PlayerAction {
  implicit val jsonFormat = jsonFormat2(apply)
}

case class GameDefinition(possibilities: Seq[PlayerAction],
                          welcome: MessageToThePlayer)

object GameDefinition {
  implicit val jsonFormat = jsonFormat2(apply)
}

