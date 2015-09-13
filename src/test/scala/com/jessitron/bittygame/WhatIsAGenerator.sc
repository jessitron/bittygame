import org.scalacheck.{Arbitrary, Prop, Gen}

import Prop.BooleanOperators

import scala.util.Random

val squaresAreGreaterExcept0or1 =
  Prop.forAll { i : Int =>
    (i != 0 && i != 1) ==> {
      (i.toLong * i) > i
    }
  }

squaresAreGreaterExcept0or1.check

// Where did it pull that i from?
val arbitraryInt = implicitly[Arbitrary[Int]]

val intGen = arbitraryInt.arbitrary

val betterIntGen = intGen.suchThat(!List(0,1).contains(_))


val r = new scala.util.Random(42)
val p = new Gen.Parameters {
  override val rng: Random = r
  override val size: Int = 10
}

betterIntGen(p)
betterIntGen(p)
betterIntGen(p)

val carefullyGenerated =
  Prop.forAll(betterIntGen) { i : Int =>
    (i.toLong * i) > i
  }
carefullyGenerated.check

val carefullyGeneratedWithArgLabel =
  Prop.forAll(betterIntGen :| "i") { i : Int =>
    ((i.toLong * i) > 100) :| s"why wasn't ${i * i} > 100?"
  }
carefullyGeneratedWithArgLabel.check

