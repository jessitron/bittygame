package com.jessitron.bittygame

case class PlayerAction(trigger: String, printedResponse: String)

case class GameDefinition(possibilities: Seq[PlayerAction])
