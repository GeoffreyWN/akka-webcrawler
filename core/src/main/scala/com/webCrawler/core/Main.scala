package com.webCrawler.core

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }

object Main extends App {
  println(s" Current Time ${System.currentTimeMillis}")

  val system     = ActorSystem("akkaWebCrawler")
  val webCrawler = system.actorOf(Props[CrawlServer], "CrawlServer")
  val main       = system.actorOf(Props[Main](new Main(webCrawler, "https://www.nation.co.ke/", 2)), "siteActor")
}

class Main(crawler: ActorRef, url: String, depth: Integer) extends Actor {
  import CrawlServer._

  crawler ! CrawlRequest(url, depth)

  override def receive: Receive = { case CrawlResponse(root, links) =>
    println(s"Root: $root")
    println(s"Links: ${links.toList.sortWith(_.length < _.length).mkString("\n")}")
    println("****************")
    println(s"Current Time ${System.currentTimeMillis}")
  }
}
