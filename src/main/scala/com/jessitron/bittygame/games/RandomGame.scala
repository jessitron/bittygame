package com.jessitron.bittygame.games

import java.nio.file.{Path, Files}

import com.jessitron.bittygame.crux.{PlayerAction, GameDefinition}
import com.jessitron.bittygame.web.identifiers.GameDefinitionKey
import org.scalacheck.Gen

import scala.io.Source

object RandomGame {
  import Util._
  lazy val defaultGameGen = new RandomGame(
    readResource("/firstPartOfSentence.txt"),
    readResource("/secondPartOfSentence.txt"),
    readResource("/nouns.txt"),
    readResource("/verbs.txt")
  )
  lazy val defaultNamer = new RandomName(readResource("/names.txt"))

  def create() = defaultGameGen.create()

  def name(excluding: Seq[String]) = defaultNamer.name(excluding)

}

class RandomName(names: Seq[String]) {
  val nameGen = for {
    happyName <- Gen.oneOf("pinkiepie", "heidegger", names:_*)
    stupidNumber <- Gen.choose(1, 10000)
  } yield s"$happyName$stupidNumber"

  def name(excluding: Seq[String]): GameDefinitionKey = try {
    Util.untilYouGetOne(nameGen.suchThat(!excluding.contains(_)).sample)
  } catch {
    case t : RuntimeException => throw new RuntimeException (s"excluded: $excluding", t)
  }
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
    verb <- Gen.oneOf("take", "open", verbs:_*)
    noun <- Gen.oneOf("door", "book", nouns:_*)
    message <- funMessage(3)
  } yield PlayerAction.printing(s"$verb $noun", message)

  private def victory: Gen[PlayerAction] = for {
    verb <- Gen.oneOf("take", "open", verbs:_*)
    noun <- Gen.oneOf("door", "book", nouns:_*)
  } yield PlayerAction.victory(s"$verb $noun", "Woot! You win!")

  def funGameGen(numActions: Int) : Gen[GameDefinition] = for {
    welcomeMessage <- funMessage(5)
    actions <- Gen.listOfN(numActions, funAction)
    winningAction <- victory.suchThat(v => !actions.exists(_.conflictsWith(v)))
  } yield GameDefinition(scala.util.Random.shuffle(actions :+ winningAction), welcomeMessage)

  def create(): GameDefinition = Util.untilYouGetOne(funGameGen(numActions = 5).sample)

}

object Util {
  // these belong in a microlib

  def readResource(name: String): Seq[String] = {
    val source = Source.fromURL(this.getClass().getResource(name))
    try {
      source.getLines().toList
    } finally {
      source.close()
    }
  }

  def untilYouGetOne[T](how: => Option[T], limit: Int = 1000): T =
    Iterator.continually(how)
      .take(limit)
      .collectFirst{ case Some(x) => x}
      .getOrElse(throw new RuntimeException(s"Didn't get one after $limit tries"))
}

