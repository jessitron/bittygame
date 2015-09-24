package com.jessitron.bittygame.serialization

import com.jessitron.bittygame.crux.{Acquire, Item, TurnResult}
import com.jessitron.bittygame.gen.TurnResultGen
import org.scalacheck.{Prop, Properties}
import org.scalatest.FunSpec

import spray.json._
import spray.json.DefaultJsonProtocol._

class TurnResultSerializationProperties
  extends Properties("TurnResult <-> JSON")
  with TurnResultGen {

  property("Round trip") =
    Prop.forAll {
      turnResult: TurnResult =>

        /* TurnResult -> JSON */
        val serialized: String = turnResult.toJson.compactPrint

        /* JSON -> TurnResult */
        val deserialized = serialized.parseJson.convertTo[TurnResult]

        Prop(turnResult == deserialized).label(s"Not successfully parsed: $serialized")
    }


}

class TurnResultSerializationExamples extends FunSpec {

  describe("the serialization") {

    it("can serialize the item") {

      val thing = Item("poo")

      val serialized: JsValue = thing.toJson
      val deserialized = serialized.convertTo[Item]

      assert(deserialized == thing)
    }

    it("can handle acquire") {
      val thing: TurnResult = Acquire(Item("poo"))

      val serialized: JsValue = thing.toJson
      val asMap = serialized.convertTo[Map[String, JsValue]]

      assert(asMap("type") == JsString("acquire"))

    }
  }

}