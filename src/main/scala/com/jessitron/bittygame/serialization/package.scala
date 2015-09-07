package com.jessitron.bittygame

import com.jessitron.bittygame.crux._
import spray.json._
import spray.json.DefaultJsonProtocol._

package object serialization {

  implicit val thingThatCanHappenWriter: JsonWriter[ThingThatCanHappen] = new JsonWriter[ThingThatCanHappen] {
    override def write(obj: ThingThatCanHappen): JsValue = {
      val fields = obj match {
        case ExitGame => Map("type" -> "exit")
        case Print(m) => Map("type" -> "print", "message" -> m)
      }
      fields.toJson
    }
  }
  implicit val playerActionFormat: JsonFormat[PlayerAction] = jsonFormat2(PlayerAction.apply)
  implicit val gameDefinitionFormat: JsonFormat[GameDefinition] = jsonFormat2(GameDefinition.apply)
}
