package com.jessitron.bittygame.crux

case class PlayerAction(trigger: String, printedResponse: MessageToThePlayer)

case class GameDefinition(possibilities: Seq[PlayerAction],
                          welcome: MessageToThePlayer)

object GameDefinition {
  def availablePossibilities(gameDef: GameDefinition,
                             state: GameState):Seq[PlayerAction] = gameDef.possibilities
}

