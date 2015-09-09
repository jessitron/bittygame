package com.jessitron.bittygame.web.messages

import spray.json.DefaultJsonProtocol._

case class CreateRandomGameResponse(created: String, first_turn_url: String)
object CreateRandomGameResponse {
  implicit val format = jsonFormat2(apply)
}
