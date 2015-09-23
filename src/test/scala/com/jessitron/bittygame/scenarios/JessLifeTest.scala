package com.jessitron.bittygame.scenarios

import com.jessitron.bittygame.crux.Turn
import com.jessitron.bittygame.web.BittyGameServiceInfrastructure
import org.scalatest.FunSpec

class JessLifeTest extends FunSpec with BittyGameServiceInfrastructure {

  val scen = JessLife.scenario
  val turns = Seq("pay for college",
    "do all your homework",
    "pay for college",
    "get drunk",
    "get drunk a lot",
    "summer internship",
    "go to college",
    "take a summer internship",
    "get programming job"
  )

  describe("This one example that appears to fail") {



    create(scen)

    val gameID = callBeginGame(scen.title).gameID

    turns.foreach { move =>
      println(s"> $move")
      val response = callTakeTurn(gameID, move)
      println(s"got: ${response.instructions}")
    }

    // this results in a win, and shouldn't. looks like "go to college" affected state

  }

  describe("the inner bits") {

    val scen = JessLife.scenario

    val (beginState, happ) = Turn.firstTurn(scen)

    println("beginState: " + beginState)

    var state = beginState
    turns.foreach { move =>
      println(s"> $move")
      val (newState, happenings) = Turn.act(scen)(state, move)
      state = newState
      println(s"Happenings: $happenings")
      println(s"State: $state")
    }


  }



}
