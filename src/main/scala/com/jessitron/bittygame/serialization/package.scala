package com.jessitron.bittygame

import com.jessitron.bittygame.crux._
import spray.json._
import spray.json.DefaultJsonProtocol._

package object serialization {

  implicit val thingThatCanHappenWriter: JsonFormat[ThingThatCanHappen] = new JsonFormat[ThingThatCanHappen] {
    override def write(obj: ThingThatCanHappen): JsValue = {
      val fields = obj match {
        case ExitGame => Map("type" -> "exit")
        case Print(m) => Map("type" -> "print", "message" -> m)
      }
      fields.toJson
    }

    override def read(json: JsValue): ThingThatCanHappen = ???
  }
  implicit val playerActionFormat: RootJsonFormat[PlayerAction] = jsonFormat2(PlayerAction.apply)
  implicit val gameDefinitionFormat: RootJsonFormat[GameDefinition] = jsonFormat2(GameDefinition.apply)

  implicit val itemFormat: RootJsonFormat[Item] = jsonFormat1(Item.apply)
  implicit val gameStateFormat: RootJsonFormat[GameState] = jsonFormat1(GameState.apply)

  implicit val whatHappensWriter: RootJsonFormat[WhatHappens] = jsonFormat1(WhatHappens.apply)
}
