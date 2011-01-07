package scalaz
package http
package netty

import Util.Nel._
import Scalaz._
import response.Response

import org.jboss.netty.handler.codec.http.{HttpMessage, HttpResponse, HttpResponseStatus}
import org.jboss.netty.buffer.{ChannelBuffers, ChannelBufferOutputStream}

trait HttpNettyResponse {
  val response: HttpMessage with HttpResponse

  def respond[OUT[_]](res: Response[OUT])(implicit e: Each[OUT]) {
    response.setStatus(HttpResponseStatus.valueOf(res.line.status.toInt))

    res.headers.foreach { case (h, v) => response.addHeader(h, v.mkString) }

    val buffer = ChannelBuffers.dynamicBuffer
    val out = new ChannelBufferOutputStream(buffer)
    response.setContent(buffer)
    e.each[Byte](res.body, out.write(_))
  }
}

trait HttpNettyResponses {
  implicit def HttpNettyResponseResponse(r: HttpMessage with HttpResponse): HttpNettyResponse = new HttpNettyResponse {
    val response = r
  }

  implicit def ResponseHttpNettyResponse(response: HttpNettyResponse) = response.response
}

object HttpNettyResponse extends HttpNettyResponses