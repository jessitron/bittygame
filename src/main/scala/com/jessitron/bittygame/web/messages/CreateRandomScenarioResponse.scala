package com.jessitron.bittygame.web.messages

import spray.json.DefaultJsonProtocol._

case class CreateRandomScenarioResponse(created: String, first_turn_url: String)
object CreateRandomScenarioResponse {
  implicit val format = jsonFormat2(apply)
}
