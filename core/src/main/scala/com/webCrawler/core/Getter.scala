package com.webCrawler.core

import java.net.URL
import scala.jdk.CollectionConverters.IteratorHasAsScala
import scala.util.{Failure, Success}
import akka.actor.{Actor, Status}
import org.jsoup.Jsoup

import scala.concurrent.ExecutionContextExecutor


object Getter {
  case object Done
  case object Abort
}

class Getter(url: String, depth: Int) extends Actor {
  import Getter._
  import LinkChecker._

  implicit val ec: ExecutionContextExecutor = context.dispatcher

  private val currentHost = new URL(url).getHost

  WebClient.get(url) onComplete {
    case Success(body) => self ! body
    case Failure(ex)   => self ! Status.Failure(ex)
  }

  private def getAllLinks(content: String): Iterator[String] = {
    Jsoup.parse(content, this.url).select("a[href]").iterator().asScala.map(_.absUrl("href"))
  }

  override def receive: Receive = {
    case body: String      =>
      getAllLinks(body)
        .filter(link => link != null && link.nonEmpty)
        .filter(link => !link.contains("mailto"))
        .filter(link => currentHost == new URL(link).getHost)
        .foreach(context.parent ! CheckUrl(_, depth))
      stop()
    case _: Status.Failure => stop()
    case Abort => stop()
  }

  private def stop(): Unit = {
    context.parent ! Done
    context.stop(self)
  }
}
