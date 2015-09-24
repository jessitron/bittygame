package com.jessitron.bittygame.scenarios

import com.jessitron.bittygame.crux._

object JessLife {

  private val welcome = "You are in high school. What do you do?"

  // Items
  private val collegeEnrollment = Item("college enrollment")
  private val partTimeJob = Item("part-time menial job")
  private val loveOfProgramming = Item("love of programming")
  private val steadyIncome = Item("steady income")
  private val husband = Item("husband")
  private val children = Item("children")
  private val house = Item("house")
  private val chanceToMeetTed = Item("option to go to the JUG")
  private val speakingMentor = Item("mentor for speaking at conferences")
  private val scalaJob = Item("functional programming work")

  // Stats
  private val grades = Stat("grades", 0, 4, 2)
  private val publicSpeaking = Stat("public speaking", 0, 8, 2)
  private val programmingCred = Stat("credibility", 0, 8, 1)
  private val tasteForAlcohol = Stat("acquired tastes", 0, 8, 1)

  // Opportunities
  private val pleaseTheTeachers = Opportunity.printing("do all my homework",
    "Your teachers love you.").andIncrease(grades.name).onlyIf(NotHas(collegeEnrollment)).onlyIf(StatLowerThan(tasteForAlcohol.name, 3))

  private val drinking = Opportunity.printing("get drunk a lot",
    "All the other kids are doing it. Now you have people to hang out with."). // TODO: lower grades
    andIncrease(tasteForAlcohol.name).onlyIf(NotHas(steadyIncome))

  private val drama = Opportunity.printing("help with my grandmother's plays",
   """While your grandmother is the director at the local college, you take the parts of missing actors at rehearsals, and participate in the travelling drama troupe. You get comfortable on stage."""
  ).onlyIf(NotHas(collegeEnrollment)).andIncrease(publicSpeaking.name).andIncrease(publicSpeaking.name)

  private val takeTest = Opportunity.printing("take standardized tests",
    "You got a great score! Now you'll have scholarships").andIncrease(grades.name).onlyIf(NotHas(collegeEnrollment))

  private val goToCollege = Opportunity.printing("go to college for free",
    "They're so impressed with you, you get a full ride to study Physics").
    andProvides(collegeEnrollment).
    onlyIf(StatAtLeast(grades.name, 4)).
    onlyIf(NotHas(collegeEnrollment))

  private val payForCollege = Opportunity.printing("pay for college",
    "You get a part-time job so you won't have too much debt.").
    andProvides(partTimeJob).
    andProvides(collegeEnrollment).
    behindObstacle(StatAtLeast(grades.name, 2), "Your grades are too bad.").
    onlyIf(NotHas(collegeEnrollment))

  private val computerInternship = Opportunity.printing("take a summer internship",
    "You stay with your aunt and work as a programmer for the summer. Fun!").
    onlyIf(Has(collegeEnrollment)).
    andProvides(loveOfProgramming).
    behindObstacle(Has(partTimeJob), "Oh, no, you would lose your part-time job.").
    onlyIf(NotHas(loveOfProgramming))

  private val amdocs = Opportunity.printing("get a programming job",
    """You have your choice of jobs, all making a good salary. Your favorite is Amdocs, so you move to St. Louis to start there.
       You learn a ton, make lots of friends, and start a career that leads everywhere.
    """).onlyIf(Has(loveOfProgramming)).andProvides(steadyIncome).onlyIf(NotHas(steadyIncome))

  private val getMarried = Opportunity.printing("get married",
     """The months of reading Sports Illustrated and Car & Driver pay off. You find a very nice fellow. He wants a house before babies.""").
     onlyIf(Has(steadyIncome)).andProvides(husband)

  private val buyHouse = Opportunity.printing("buy a house",
   """After viewing 92 houses, you finally pick one. Good school district, quaint street, and wood trim that hasn't been painted.""").
    onlyIf(Has(steadyIncome)).andProvides(house).onlyIf(NotHas(house))

  private val babies = Opportunity.printing("have babies",
  """Two healthy, happy daughters. Now to keep them alive, and hopefully, keep them silly. Growing up is overrated.""").
  onlyIf(Has(house)).onlyIf(Has(husband)).andProvides(children).onlyIf(NotHas(children))

  private val meetTed = Opportunity.printing("check my phone at the right time",
   """Facebook says that your favorite speaker is at the Java User Group tonight. If you rush, you could ask him for a beer. Stay home, or go network?""").
  onlyIf(Has(steadyIncome)).andProvides(chanceToMeetTed).onlyIf(NotHas(chanceToMeetTed))

  private val stayHome = Opportunity.printing("stay home",
  """You have a nice night at home, and a nice life as an enterprise software developer.""").
    onlyIf(Has(chanceToMeetTed)).andExit

  private val goToJUG = Opportunity.printing("go network",
  """You pop down to the user group and suggest drinks. While at the bar, someone asks, 'Have you ever thought about speaking at conferences?' """).
    onlyIf(Has(chanceToMeetTed)).onlyIf(NotHas(speakingMentor)).
    andProvides(speakingMentor)

  private val speakAtConferences = Opportunity.printing("speak at conferences",
  """With a little encouragement, feedback, and some forwarded opportunities, you do fourteen conference talks in your first year!""").
    onlyIf(Has(speakingMentor)).andIncrease(publicSpeaking.name).andIncrease(programmingCred.name)

  private val blog = Opportunity.printing("write blog posts",
  """While learning topics to speak about, you post things that you learn to your blog. Small ones, large ones, sometimes with pictures.""").
    onlyIf(Has(speakingMentor)).andIncrease(programmingCred.name)

  private val monsantoJob = Opportunity.printing("get new job",
  """Monsanto has an opening for a Scala developer! You're close enough. There, you learn a ton about Scala, biotech, distributed systems, concurrency. This is great!""").
  onlyIf(StatAtLeast(programmingCred.name, 2)).andIncrease(programmingCred.name).andProvides(scalaJob).onlyIf(NotHas(scalaJob))
  
  private val speakAtStrangeloop = Opportunity.victory("speak at StrangeLoop",
    """You get to speak at StrangeLoop, the best conference of all! Victory!""").
    onlyIf(Has(speakingMentor)).
    behindObstacle(StatLowerThan(programmingCred.name, 4), "Your abstract is not accepted.").
    behindObstacle(StatLowerThan(publicSpeaking.name, 5), "Your abstract is not accepted.")

  private val gradSchool = Opportunity.printing("go to grad school",
    "You get into a physics PhD program, but your heart isn't in it. The politics! The competitiveness! Of academia. burnout. :-(").
    onlyIf(Has(collegeEnrollment)).onlyIf(NotHas(steadyIncome)).
    andExit

  val scenario = Scenario("jesslife",
    Seq(gradSchool, amdocs, computerInternship, payForCollege, goToCollege,
        takeTest, drinking, pleaseTheTeachers, drama, getMarried, buyHouse,
      speakAtStrangeloop, speakAtConferences, monsantoJob, blog, goToJUG, stayHome, meetTed),
    welcome,
    Seq(grades, programmingCred, publicSpeaking, tasteForAlcohol),
    Seq(loveOfProgramming, steadyIncome, collegeEnrollment, partTimeJob, house, husband, children, speakingMentor, scalaJob))
}
