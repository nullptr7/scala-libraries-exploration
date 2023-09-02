package com.github.nullptr7

import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.util.EntityUtils

import java.net.URI
import scala.util.{ Failure, Success, Try }

class SampleResterService(httpClient: CloseableHttpClient) {
  private val uriReq: URI = new URIBuilder()
    .setScheme("https")
    .setHost("https")
    .setPath("reqres.in/api/users?page=1")
    .build()

  private val request: HttpGet = new HttpGet(uriReq)

  def sendAndReceive: String = {

    val response = httpClient.execute(request)
    (response.getStatusLine.getStatusCode match {
      case 200 =>
        Try {
          val ent     = response.getEntity
          val rString = EntityUtils.toString(ent)
          rString
        }
      case _   => Success("Bad Response")
    }) match {
      case Failure(exception) =>
        exception.printStackTrace()
        "Failure"
      case Success(value)     => value
    }

  }

}
