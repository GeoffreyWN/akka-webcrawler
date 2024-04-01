package com.webCrawler.core

import java.util.concurrent.Executors
import scala.concurrent.{ Future, Promise }
import com.ning.http.client.{ AsyncCompletionHandler, AsyncHttpClient, AsyncHttpClientConfig, Response }

object WebClient {
  val config = new AsyncHttpClientConfig.Builder()
  val client = new AsyncHttpClient(config.setFollowRedirect(true).setExecutorService(Executors.newWorkStealingPool(64)).build())

  def get(url: String): Future[String] = {
    val promise = Promise[String]()
    val request = client.prepareGet(url).build()

    client.executeRequest(
      request,
      new AsyncCompletionHandler[Response]() {

        override def onCompleted(response: Response): Response = {
          promise.success(response.getResponseBody)
          response
        }

        override def onThrowable(t: Throwable): Unit = {
          promise.failure(t)
        }
      }
    )
    promise.future
  }

}
