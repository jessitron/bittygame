package com.jessitron.bittygame.scenarios

import com.jessitron.bittygame.crux.{Has, Acquire, Item, Opportunity}

object JessLife {

  val welcome = "You are in high school. What do you do?"

  // Items
  val collegeEnrollment = Item("college enrollment")
  val partTimeJob = Item("part-time menial job")
  val loveOfProgramming = Item("love of programming")

  // Opportunities
  val pleaseTheTeachers = Opportunity.printing("do all your homework", "Your teachers love you.") // TODO: raise grades

  val drinking = Opportunity.printing("get drunk a lot", "All the other kids are doing it. Now you have people to hang out with.") // TODO: lower grades

  val takeTest = Opportunity.printing("take standardized tests", "You got a great score! Now you'll have scholarships") // TOTO: raise grades

  val goToCollege = Opportunity.printing("go to college for free", "They're so impressed with you, you get a full ride to study Physics").andProvides(collegeEnrollment) // TODO: only if grades are good enough

  val payForCollege = Opportunity.printing("pay for college", "You get a part-time job so you won't have too much debt.").andProvides(partTimeJob).andProvides(collegeEnrollment) // TODO: only if grades not bad

  val computerInternship = Opportunity.printing("take a summer internship",
    "You stay with your aunt and work as a programmer for the summer. Fun!").
    onlyIf(Has(collegeEnrollment)).
    andProvides(loveOfProgramming)
   // .withObstacle(Has(partTimeJob), "Oh, no, you would lose your part-time job.")// TODO: only if not has part time job!



}
