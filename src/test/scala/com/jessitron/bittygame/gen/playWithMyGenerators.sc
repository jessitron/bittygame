import com.jessitron.bittygame.gen.ActionConditionGen
import org.scalacheck.Gen


object Play extends ActionConditionGen {
  val thisshouldntwork = conditionGen(Seq()).sample

  val poo = conditionsGen(Seq()).sample
}

Play.thisshouldntwork

Play.poo

Gen.choose(0,0).sample

Gen.listOfN(0, Gen.oneOf(Seq[Int]())).sample