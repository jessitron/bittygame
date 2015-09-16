package com.jessitron.bittygame

import com.jessitron.bittygame.crux._
import spray.json._
import spray.json.DefaultJsonProtocol._

package object serialization {

  implicit def actionConditionWriter(implicit itemFormat: JsonFormat[Item]): JsonFormat[ActionCondition] = new JsonFormat[ActionCondition] {
    override def write(obj: ActionCondition): JsValue = {
      val fields = obj match {
        case Has(item) => Map("type" -> JsString("has"), "item" -> itemFormat.write(item))
      }
      fields.toJson
    }

    private def fail(why: String, json: JsValue): Nothing =
      throw new RuntimeException(s"Could not deserialize ActionCondition; $why. Received:${json.prettyPrint}")

    override def read(json: JsValue): ActionCondition = {
      val mappy = json.asInstanceOf[JsObject].fields
      mappy.getOrElse("type", fail("no type", json)) match {
        case JsString("has") => Has(itemFormat.read(mappy.getOrElse("item", fail("Has needs item", json))))
      }
    }
  }

  implicit def thingThatCanHappenWriter(implicit itemFormat: JsonFormat[Item]): JsonFormat[ThingThatCanHappen] = new JsonFormat[ThingThatCanHappen] {

    override def write(obj: ThingThatCanHappen): JsValue = {
      val fields = obj match {
        case ExitGame => Map("type" -> "exit").mapValues(JsString(_))
        case Print(m) => Map("type" -> "print", "message" -> m).mapValues(JsString(_))
        case Win      => Map("type" -> "win").mapValues(JsString(_))
        case Acquire(it)  => Map("type" -> JsString("acquire"), "item" -> itemFormat.write(it))
        case IDontKnowHowTo(s) => Map("type" -> "unknown", "what" -> s).mapValues(JsString(_))
        case CantDoThat(s) => Map("type" -> "denied", "why" -> s).mapValues(JsString(_))
      }
      fields.toJson
    }

    private def fail(why: String, json: JsValue): Nothing =
      throw new RuntimeException(s"Could not deserialize ThingThatCanHappen; $why. Received:${json.prettyPrint}")

    override def read(json: JsValue): ThingThatCanHappen = {
      val mappy = json.asInstanceOf[JsObject].fields
      mappy.getOrElse("type", fail("no type", json)).asInstanceOf[JsString].value match {
        case "exit"  => ExitGame
        case "win"   => Win
        case "acquire" => Acquire(itemFormat.read(mappy.getOrElse("item", fail("acquire needs item", json))))
        case "print" => Print(mappy.getOrElse("message", fail("print needs message", json)).asInstanceOf[JsString].value)
        case "unknown" => IDontKnowHowTo(mappy.getOrElse("what", fail("unknown needs what", json)).asInstanceOf[JsString].value)
        case "denied"  => CantDoThat(mappy.getOrElse("why", fail("denied needs why", json)).asInstanceOf[JsString].value)
      }
    }
  }

  implicit val itemFormat: RootJsonFormat[Item] = jsonFormat1(Item.apply)
  implicit val whatHappensWriter: RootJsonFormat[WhatHappens] = jsonFormat1(WhatHappens.apply)
  implicit val obstacleWriter: JsonFormat[Obstacle] = jsonFormat2(Obstacle.apply)

  implicit val playerActionFormat: RootJsonFormat[Opportunity] = jsonFormat4(Opportunity.apply)
  implicit val scenarioFormat: RootJsonFormat[Scenario] = jsonFormat2(Scenario.apply)

  implicit val gameStateFormat: RootJsonFormat[GameState] = jsonFormat1(GameState.apply)
}
