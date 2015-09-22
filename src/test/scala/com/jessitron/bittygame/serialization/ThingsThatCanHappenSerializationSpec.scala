package com.jessitron.bittygame.serialization

import com.jessitron.bittygame.crux.{Acquire, Item, TurnResult}
import com.jessitron.bittygame.gen.ThingThatCanHappenGen
import org.scalatest.{FunSpec, Assertions, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import spray.json._
import spray.json.DefaultJsonProtocol._

class ThingsThatCanHappenSerializationSpec
  extends PropSpec with GeneratorDrivenPropertyChecks with Assertions
  with ThingThatCanHappenGen {

  property("Round trip") {
    forAll { turnResult: TurnResult =>
      val serialized: String = turnResult.toJson.compactPrint
      val deserialized = serialized.parseJson.convertTo[TurnResult]
      assert(turnResult === deserialized, s"Not successfully parsed: $serialized")
    }
  }

}

class ThingsThatCanHappenSerializationExamples extends FunSpec {

  describe("the serialization") {

    it("can serialize the item") {

      val thing = Item("poo")

      val serialized: JsValue = thing.toJson
      val deserialized = serialized.convertTo[Item]

      assert(deserialized == thing)
    }

    it ("can handle acquire" ) {
      val thing: TurnResult = Acquire(Item("poo"))

      val serialized: JsValue = thing.toJson
      val asMap = serialized.convertTo[Map[String,JsValue]]

      assert(asMap("type") == JsString("acquire"))

    }
  }

}