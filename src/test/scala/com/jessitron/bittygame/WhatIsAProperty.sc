import org.scalacheck.Test.TestCallback
import org.scalacheck.{Test, Prop, Gen}

import scala.util.Random

val trueProperty = Prop(4 > 2)

trueProperty.check // side effect of printing
val r = new scala.util.Random(42)
val p = new Gen.Parameters {
  override val rng: Random = r
  override val size: Int = 10
}

val result = trueProperty(p)

result.proved // true. This one is just a Boolean.

// Now let's expand the circumstances.
// Perhaps what is true for 2 is true for all integers.
val squaresAreGreater =
  Prop.forAll { i : Int =>
    (i * i) > i
  }

val tp = Test.Parameters.default.withRng(r)

// Results as data
val wellAreThey = Test.check(tp, squaresAreGreater)

println("\nSquares are greater? ")
squaresAreGreater.check(tp)


// OK, the business says it's ok in the 0 case.
import Prop.BooleanOperators
val squaresAreGreaterExcept0 =
  Prop.forAll { i : Int =>
    (i != 0) ==> {
      (i * i) > i
    }
  }
squaresAreGreaterExcept0.check(tp)
// oops, max-int exceeded. Try calling toLong on the first i

// it still fails on 1
import Prop.BooleanOperators
val squaresAreGreaterExcept0or1 =
  Prop.forAll { i : Int =>
    (i != 0 && i != 1) ==> {
      (i.toLong * i) > i
    }
  }
println("Squares are greater, with exclusion: ")
squaresAreGreaterExcept0or1.check(tp)

// because they're data, they compose
import Prop.BooleanOperators
val addMoreChecks =
  Prop.forAll { i : Int =>
    (i != 0 && i != 1) ==> {
      (i.toLong * i) > i
    } &&
      (i.toLong + 2 > i)
  }

println("more checks: ")
addMoreChecks.check