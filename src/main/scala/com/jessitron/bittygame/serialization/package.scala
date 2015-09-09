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

    private def fail(why: String, json: JsValue): Nothing =
      throw new RuntimeException(s"Could not deserialize ThingThatCanHappen; $why. Received:${json.prettyPrint}")

    override def read(json: JsValue): ThingThatCanHappen = {
      val mappy = json.convertTo[Map[String, String]]
      mappy.getOrElse("type", fail("no type", json)) match {
        case "exit"  => ExitGame
        case "print" => Print(mappy.getOrElse("message", fail("print needs message", json)))
      }
    }
  }

  implicit val playerActionFormat: RootJsonFormat[PlayerAction] = jsonFormat2(PlayerAction.apply)
  implicit val gameDefinitionFormat: RootJsonFormat[GameDefinition] = jsonFormat2(GameDefinition.apply)

  implicit val itemFormat: RootJsonFormat[Item] = jsonFormat1(Item.apply)
  implicit val gameStateFormat: RootJsonFormat[GameState] = jsonFormat1(GameState.apply)

  implicit val whatHappensWriter: RootJsonFormat[WhatHappens] = jsonFormat1(WhatHappens.apply)
}
