package com.jessitron.bittygame.games

import java.nio.file.{Path, Files}

import com.jessitron.bittygame.crux.{PlayerAction, GameDefinition}
import org.scalacheck.Gen

import scala.io.Source

object RandomGame {

  lazy val funSentence: Gen[String] = for {
    firstPart <- Gen.oneOf("You see", "All around you swarm", readResource("firstPartOfSentence.txt") :_*)
    secondPart <- Gen.oneOf("monkeys staring at you", "a giant spider", readResource("secondPartOfSentence.txt") :_*)
  } yield s"$firstPart $secondPart"

  def funMessage(maxSentences: Int): Gen[String] = for {
    numberOfSentences <- Gen.choose(1, maxSentences)
    sentences <- Gen.listOfN(numberOfSentences, funSentence)
  } yield sentences.mkString(". ")

  def funAction: Gen[PlayerAction] = for {
    verb <- Gen.oneOf("take", "open", readResource("verbs.txt"))
    noun <- Gen.oneOf("door", "book", readResource("nouns.txt"))
    message <- funMessage(3)
  } yield PlayerAction(s"$verb $noun", message)

  def funGameGen(size: Int) : Gen[GameDefinition] = for {
    welcomeMessage <- funMessage(5)
    actions <- Gen.listOfN(size, funAction)
  } GameDefinition(actions, welcomeMessage)

  def create(): GameDefinition = untilYouGetOne(funGameGen(10).sample)

  def untilYouGetOne[T](how: => Option[T], limit: Int = 1000): T =
    Iterator.continually(how)
      .take(limit)
      .collectFirst{ case Some(x) => x}
      .getOrElse(throw new RuntimeException(s"Didn't get one after $limit tries"))


  def readResource(name: String): Seq[String] = {
    val source = Source.fromURL(this.getClass().getResource(name))
    try {
      source.getLines().toSeq
    } finally {
      source.close()
    }
  }
}

