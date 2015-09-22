package com.jessitron.bittygame.serialization

import com.jessitron.bittygame.crux.{Acquire, Item, ThingThatCanHappen}
import org.scalatest.{FunSpec, Assertions, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import spray.json._
import spray.json.DefaultJsonProtocol._

import com.jessitron.bittygame.gen.ThingThatCanHappenGen._

class ThingsThatCanHappenSerializationSpec
  extends PropSpec with GeneratorDrivenPropertyChecks with Assertions {

  property("Round trip") {
    forAll { thing: ThingThatCanHappen =>
      val serialized: String = thing.toJson.compactPrint
      val deserialized = serialized.parseJson.convertTo[ThingThatCanHappen]
      assert(thing === deserialized, s"Not successfully parsed: $serialized")
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
      val thing: ThingThatCanHappen = Acquire(Item("poo"))

      val serialized: JsValue = thing.toJson
      val deserialized = serialized.convertTo[Map[String,JsValue]]

      assert(deserialized.contains("type"))

    }
  }

}