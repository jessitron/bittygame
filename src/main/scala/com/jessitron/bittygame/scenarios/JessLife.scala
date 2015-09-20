package com.jessitron.bittygame.scenarios

import com.jessitron.bittygame.crux._

object JessLife {

  private val welcome = "You are in high school. What do you do?"

  // Items
  private val collegeEnrollment = Item("college enrollment")
  private val partTimeJob = Item("part-time menial job")
  private val loveOfProgramming = Item("love of programming")

  // Stats
  private val grades = Stat("grades", 0, 4, 2)

  // Opportunities
  private val pleaseTheTeachers = Opportunity.printing("do all your homework",
    "Your teachers love you.").andIncrease(grades.name)

  private val drinking = Opportunity.printing("get drunk a lot",
    "All the other kids are doing it. Now you have people to hang out with.") // TODO: lower grades

  private val takeTest = Opportunity.printing("take standardized tests",
    "You got a great score! Now you'll have scholarships").andIncrease(grades.name)

  private val goToCollege = Opportunity.printing("go to college",
    "They're so impressed with you, you get a full ride to study Physics").
    andProvides(collegeEnrollment) // TODO: only if grades are good enough

  private val payForCollege = Opportunity.printing("pay for college",
    "You get a part-time job so you won't have too much debt.").
    andProvides(partTimeJob).
    andProvides(collegeEnrollment) // TODO: only if grades not bad

  private val computerInternship = Opportunity.printing("take a summer internship",
    "You stay with your aunt and work as a programmer for the summer. Fun!").
    onlyIf(Has(collegeEnrollment)).
    andProvides(loveOfProgramming).
    behindObstacle(Has(partTimeJob), "Oh, no, you would lose your part-time job.")

  private val amdocs = Opportunity.victory("get programming job",
    """You have your choice of jobs, all making a good salary. Your favorite is Amdocs, so you move to St. Louis to start there.
       You learn a ton, make lots of friends, and start a career that leads everywhere.
    """).onlyIf(Has(loveOfProgramming))

  private val gradSchool = Opportunity.printing("go to grad school",
    "You get into a physics PhD program, but your heart isn't in it. The politics! The competitiveness! Of academia. burnout. :-(").
    onlyIf(Has(collegeEnrollment)).
    andExit

  val scenario = Scenario("jesslife",
    Seq(gradSchool, amdocs, computerInternship, payForCollege, goToCollege,
        takeTest, drinking, pleaseTheTeachers),
    welcome, Seq(grades))
}
