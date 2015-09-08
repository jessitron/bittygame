package com.jessitron.bittygame.crux

case class PlayerAction(trigger: String, printedResponse: MessageToThePlayer) {
  def available(gameState: GameState): Boolean = true
}

case class GameDefinition(possibilities: Seq[PlayerAction],
                          welcome: MessageToThePlayer)


