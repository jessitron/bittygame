package com.jessitron.bittygame.games

import java.nio.file.{Path, Files}

import com.jessitron.bittygame.crux.{PlayerAction, GameDefinition}
import org.scalacheck.Gen

import scala.io.Source

object RandomGame {
  import Util._
  lazy val default = new RandomGame(
    readResource("firstPartOfSentence.txt"),
    readResource("secondPartOfSentence.txt"),
    readResource("verbs.txt"),
    readResource("nouns.txt"))
}

class RandomGame(firstPartsOfSentence: Seq[String],
                 secondPartsOfSentence: Seq[String],
                 nouns: Seq[String],
                 verbs: Seq[String]) {
  private val funSentence: Gen[String] = for {
    firstPart <- Gen.oneOf("You see", "All around you swarm", firstPartsOfSentence :_*)
    secondPart <- Gen.oneOf("monkeys staring at you", "a giant spider", secondPartsOfSentence  :_*)
  } yield s"$firstPart $secondPart"

  private def funMessage(maxSentences: Int): Gen[String] = for {
    numberOfSentences <- Gen.choose(1, maxSentences)
    sentences <- Gen.listOfN(numberOfSentences, funSentence)
  } yield sentences.mkString(". ")

  private def funAction: Gen[PlayerAction] = for {
    verb <- Gen.oneOf("take", "open", verbs)
    noun <- Gen.oneOf("door", "book", nouns)
    message <- funMessage(3)
  } yield PlayerAction(s"$verb $noun", message)

  private def funGameGen(size: Int) : Gen[GameDefinition] = for {
    welcomeMessage <- funMessage(5)
    actions <- Gen.listOfN(size, funAction)
  } GameDefinition(actions, welcomeMessage)

  private def untilYouGetOne[T](how: => Option[T], limit: Int = 1000): T =
    Iterator.continually(how)
      .take(limit)
      .collectFirst{ case Some(x) => x}
      .getOrElse(throw new RuntimeException(s"Didn't get one after $limit tries"))

  def create(): GameDefinition = untilYouGetOne(funGameGen(10).sample)

}

object Util {

  def readResource(name: String): Seq[String] = {
    val source = Source.fromURL(this.getClass().getResource(name))
    try {
      source.getLines().toSeq
    } finally {
      source.close()
    }
  }
}

