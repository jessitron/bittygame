package com.jessitron.bittygame.crux

case class Stat(name: StatID, low: Int, high: Int, starting: Int) {
  assert(low <= starting, "can't start below low value")
  assert(starting <= high, "can't start above high value")
}
