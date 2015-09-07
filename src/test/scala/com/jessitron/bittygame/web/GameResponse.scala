package com.jessitron.bittygame.web

import com.jessitron.bittygame.crux.{WhatHappens, GameState}

case class GameResponse(state: GameState, instructions: WhatHappens)
