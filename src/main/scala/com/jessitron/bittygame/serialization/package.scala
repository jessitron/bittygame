package com.jessitron.bittygame

import com.jessitron.bittygame.crux._
import spray.json._
import spray.json.DefaultJsonProtocol._

package object serialization {

  implicit def actionConditionWriter(implicit itemFormat: JsonFormat[Item]): JsonFormat[Condition] = new JsonFormat[Condition] {
    override def write(obj: Condition): JsValue = {
      val fields = obj match {
        case Has(item) => Map("type" -> JsString("has"), "item" -> itemFormat.write(item))
        case Has(item) => Map("type" -> JsString("nothas"), "item" -> itemFormat.write(item))
        case x:MustBeHighEnough => val base = mustBeFormat.write(x).asJsObject; base.fields + ("type" -> JsString("YouCanDotheThing"))
      }
      fields.toJson
    }

    private def fail(why: String, json: JsValue): Nothing =
      throw new RuntimeException(s"Could not deserialize ActionCondition; $why. Received:${json.prettyPrint}")

    override def read(json: JsValue): Condition = {
      val mappy = json.asInstanceOf[JsObject].fields
      mappy.getOrElse("type", fail("no type", json)) match {
        case JsString("has") => Has(itemFormat.read(mappy.getOrElse("item", fail("Has needs item", json))))
        case JsString("nothas") => NotHas(itemFormat.read(mappy.getOrElse("item", fail("Has needs item", json))))
        case JsString("YouCanDotheThing") => mustBeFormat.read(json)
        case _ => throw new RuntimeException("I don't know how to deserialize this condition: " + json.prettyPrint)
      }
    }

    def mustBeFormat = jsonFormat2(MustBeHighEnough)
  }

  implicit def thingThatCanHappenWriter(implicit itemFormat: JsonFormat[Item]): JsonFormat[TurnResult] = new JsonFormat[TurnResult] {

    override def write(obj: TurnResult): JsValue = {
      val fields = obj match {
        case ExitGame => Map("type" -> "exit").mapValues(JsString(_))
        case Print(m) => Map("type" -> "print", "message" -> m).mapValues(JsString(_))
        case Win      => Map("type" -> "win").mapValues(JsString(_))
        case Acquire(it)  => Map("type" -> JsString("acquire"), "item" -> itemFormat.write(it))
        case IDontKnowHowTo(s) => Map("type" -> "unknown", "what" -> s).mapValues(JsString(_))
        case CantDoThat(s) => Map("type" -> "denied", "why" -> s).mapValues(JsString(_))
        case IncreaseStat(id) =>  Map("type" -> "increase", "stat" -> id).mapValues(JsString(_))
      }
      fields.toJson
    }

    private def fail(why: String, json: JsValue): Nothing =
      throw new RuntimeException(s"Could not deserialize ThingThatCanHappen; $why. Received:${json.prettyPrint}")

    override def read(json: JsValue): TurnResult = {
      val mappy = json.asInstanceOf[JsObject].fields
      mappy.getOrElse("type", fail("no type", json)).asInstanceOf[JsString].value match {
        case "exit"  => ExitGame
        case "win"   => Win
        case "acquire" => Acquire(itemFormat.read(mappy.getOrElse("item", fail("acquire needs item", json))))
        case "print" => Print(mappy.getOrElse("message", fail("print needs message", json)).asInstanceOf[JsString].value)
        case "unknown" => IDontKnowHowTo(mappy.getOrElse("what", fail("unknown needs what", json)).asInstanceOf[JsString].value)
        case "denied"  => CantDoThat(mappy.getOrElse("why", fail("denied needs why", json)).asInstanceOf[JsString].value)
        case "increase" => IncreaseStat(mappy.getOrElse("stat", fail("increase needs stat", json)).asInstanceOf[JsString].value)
      }
    }
  }

  implicit val itemFormat: RootJsonFormat[Item] = jsonFormat1(Item.apply)
  implicit val whatHappensWriter: RootJsonFormat[WhatHappens] = jsonFormat1(WhatHappens.apply)
  implicit val obstacleWriter: JsonFormat[Obstacle] = jsonFormat2(Obstacle.apply)
  implicit val statWriter: JsonFormat[Stat] = jsonFormat4(Stat.apply)

  implicit val playerActionFormat: RootJsonFormat[Opportunity] = jsonFormat4(Opportunity.apply)
  implicit val scenarioFormat: RootJsonFormat[Scenario] = jsonFormat5(Scenario.apply)

  implicit val gameStateFormat: RootJsonFormat[GameState] = jsonFormat3(GameState.apply)
}
