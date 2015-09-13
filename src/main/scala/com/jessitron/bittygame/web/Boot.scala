package com.jessitron.bittygame.web

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.Try

object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[BittyGameServiceActor], "demo-service")

  val port: Int =
  try {
    Integer.parseInt(System.getProperty("http.port"))}
  catch {
    case e: NumberFormatException => 80
   }


  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port)
}
