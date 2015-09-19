package com.jessitron.bittygame.crux

import org.scalacheck.{Prop, Properties}

object ItemProperties extends Properties("of an item") {

  property("Items with the same name are equal") =
    Prop.forAll { s: String =>
      Item(s) == Item(s)
  }

}
