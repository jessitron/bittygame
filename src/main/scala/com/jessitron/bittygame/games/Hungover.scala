package com.jessitron.bittygame.games

import com.jessitron.bittygame.crux.GameDefinition

object Hungover {

  val welcome =
    """
      |  Your head is pounding and what feels like the light of ten thousand suns
      |  is assaulting your brain directly through your eyes.
      |  
      |  Squinting, you manage to roll over on your bed, reaching out to tug the curtain across the window. Ahh, much better. The effort of moving leaves your head spinning and you collapse back on your pillow to reflect on your situation.
      |  
      |  The night before had started as just a few drinks out with friends, but had
      |  quickly progressed to a somewhat rowdier evening.
      |  
      |  As you lay there in bed some of the details start trickling back...
      |  
      |  There had definitely been shots at some point...
      |  
      |  oh God."
      |  
      |  So."
      |  many."
      |  shots."
      |  
      |  The memory of all that tequila turns your stomach and you quickly focus your
      |  attention back to the present. The clock on the wall informs you that it\'s
      |  already 1 p.m. so it\'s probably time to drag your sorry ass out of bed and get on with your day.
    """.stripMargin

  val gameDef: GameDefinition = GameDefinition(Seq(), welcome)

}
