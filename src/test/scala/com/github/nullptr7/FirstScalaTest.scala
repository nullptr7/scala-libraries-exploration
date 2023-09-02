package com.github.nullptr7

import collection.mutable.Stack
import org.scalatest._
import flatspec._
import matchers._
import org.apache.http.{ HttpEntity, ProtocolVersion }
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.message.{ BasicHeader, BasicStatusLine }
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar._

import java.io.ByteArrayInputStream
import scala.collection.mutable

class FirstScalaTest extends AnyFlatSpec with should.Matchers {
  "A Stack" should "pop values in last-in-first-out order" in {
    val stack: mutable.Stack[Int] = new Stack[Int]
    stack.push(1)
    stack.push(2)
    stack.pop() should be(2)
    stack.pop() should be(1)
  }

  it should "throw NoSuchElementException if an empty stack is popped" in {
    val emptyStack: mutable.Stack[Int] = new Stack[Int]
    a[NoSuchElementException] should be thrownBy {
      emptyStack.pop()
    }
  }

  "Mocked object" should "work" in {
    val aMockedObject = mock[CloseableHttpClient]

    val httpResponse = mock[CloseableHttpResponse]

    val httpEntity = mock[HttpEntity]

    val resultJson =
      """{
        | "key": "value"
        |}
        |""".stripMargin

    val irr        = new ByteArrayInputStream(resultJson.getBytes())
    val statusLine = new BasicStatusLine(new ProtocolVersion("https", 1, 1), 400, "Failure")

    when(httpEntity.getContent).thenReturn(irr)
    when(httpEntity.getContentType).thenReturn(new BasicHeader("Content-Type", "application/json"))
    when(httpEntity.getContentLength).thenReturn(10L)
    when(httpResponse.getEntity).thenReturn(httpEntity)
    when(httpResponse.getStatusLine).thenReturn(statusLine)
    when(aMockedObject.execute(any())).thenReturn(httpResponse)

    val resterService = new SampleResterService(aMockedObject)

    resterService.sendAndReceive shouldEqual "Bad Response"

  }

}
