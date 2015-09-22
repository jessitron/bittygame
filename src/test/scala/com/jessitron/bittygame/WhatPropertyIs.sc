import org.scalacheck.Test.Parameters
import org.scalacheck.{Arbitrary, Gen, Test, Prop}

val fourIsBigger = Prop(4 > 2)

val p: Parameters = Test.Parameters.default
val result = Test.check(p, fourIsBigger)

def square(i: Int) : Int = i * i

result.passed

import Prop.BooleanOperators

val allInt =  Gen.chooseNum(Int.MinValue, Int.MaxValue)

val squaresAreBigger =
  Prop.forAll(allInt) { i: Int =>
    (i != 1 && i != 0) ==> {
      (i.toLong * i > i) :| s"${i * i} > $i"
    }
  }

val isItBigger = Test.check(p, squaresAreBigger)
squaresAreBigger.check(p)
//---- what is a generator

val intGen: Gen[Int] =  Gen.chooseNum(Int.MinValue, Int.MaxValue)
Iterator.continually(intGen.sample).take(1000).collect { case Some(Int.MaxValue) => true}.size
val squaresAreBiggerGen =
  intGen.suchThat(i => i > 1)

Iterator.continually(squaresAreBiggerGen.sample).take(10).toSeq.foreach(println(_))
val squaresAreBiggerIndeed =
  Prop.forAll(squaresAreBiggerGen) { i: Int =>
      (i.toLong * i >= 12398413298l) :| s"${i * i} > $i"
    }
squaresAreBiggerIndeed.check(p)
Int.MaxValue
val niceInts = Gen.frequency((10, Int.MaxValue),
                             (10, 2),
  (10, Gen.choose(129, Int.MaxValue)),
  (70, Gen.choose(3, 128)))
val twoInts = Prop.forAll(niceInts :| "i1", niceInts :| "i2") {
  (i1: Int, i2:Int) =>
    i1 + i2 == i2 + i1
}
twoInts.check(p)
