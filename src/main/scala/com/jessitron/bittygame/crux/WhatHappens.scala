package com.jessitron.bittygame.crux

import spray.json._
import spray.json.DefaultJsonProtocol._

sealed trait ThingThatCanHappen
case class Print(message: MessageToThePlayer) extends ThingThatCanHappen {
  assert(message.nonEmpty, "An empty message is worse than no message at all" )
}
case object ExitGame extends ThingThatCanHappen

object ThingThatCanHappen {
  implicit val jsonWriter: JsonWriter[ThingThatCanHappen] = new JsonWriter[ThingThatCanHappen] {
    override def write(obj: ThingThatCanHappen): JsValue = {
      val fields = obj match {
        case ExitGame => Map("type" -> "exit")
        case Print(m) => Map("type" -> "print", "message" -> m)
      }
      fields.toJson
    }
  }
}

case class WhatHappens(results: Seq[ThingThatCanHappen])

object WhatHappens {
  val NothingHappens = WhatHappens(Seq())

  def thisHappens(one: ThingThatCanHappen): WhatHappens = WhatHappens(Seq(one))
}
