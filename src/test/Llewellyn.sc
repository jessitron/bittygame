val test1 = 2 < square(2)

val test2 = 3 < square(3)

def square(i: Int): Long = i.toLong * i

(Range(2, 100).map {
  _ =>
    val i = scala.util.Random.nextInt
    i < square(i)
}).contains(false)

import org.scalacheck.{Prop, Gen}

val randomInt = Gen.chooseNum(Int.MinValue, Int.MaxValue)

val squareIsBigger =
  Prop.forAll(randomInt) { i =>
   i <= square(i)
  }

squareIsBigger.check



















