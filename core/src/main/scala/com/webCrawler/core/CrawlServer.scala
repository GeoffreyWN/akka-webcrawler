package com.webCrawler.core

import akka.actor.{ Actor, ActorRef, Props }

object CrawlServer {
  case class CrawlRequest(url: String, depth: Integer)
  case class CrawlResponse(url: String, links: Set[String])
}

class CrawlServer extends Actor {
  import CrawlServer._
  import LinkChecker._

  val clients     = collection.mutable.Map[String, Set[ActorRef]]()
  val controllers = collection.mutable.Map[String, ActorRef]()

  override def receive: Receive = {
    case CrawlRequest(url, depth) =>
      val controller = controllers get url
      if (controller.isEmpty) {
        controllers += (url -> context.actorOf(Props[LinkChecker](new LinkChecker(url, depth))))
        clients += (url     -> Set.empty[ActorRef])
      }
      clients(url) += sender

    case Result(url, links) =>
      context.stop(controllers(url))
      clients(url) foreach (_ ! CrawlResponse(url, links))
      clients -= url
      controllers -= url
  }
}
