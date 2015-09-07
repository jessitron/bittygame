package com.jessitron.bittygame.serialization

import com.jessitron.bittygame.crux.ThingThatCanHappen
import org.scalacheck.Shrink
import org.scalatest.{Assertions, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import spray.json._
import spray.json.DefaultJsonProtocol._

import com.jessitron.bittygame.gen.ThingThatCanHappenGen._

class ThingsThatCanHappenSerializationSpec
  extends PropSpec with GeneratorDrivenPropertyChecks with Assertions {

  property("There is always a type field") {
    forAll { (thing: ThingThatCanHappen) =>
      val serialized: JsValue = thing.toJson
      val deserializedAsMap = serialized.convertTo[Map[String,String]]

      assert(deserializedAsMap.contains("type"))
    }
  }

}
