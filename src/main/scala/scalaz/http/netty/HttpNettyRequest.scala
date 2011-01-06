package scalaz
package http
package netty

import Scalaz._
import request.{Method, RequestHeader, Request}
import request.Line.line
import request.Uri.uri

import collection.JavaConverters._

import org.jboss.netty.handler.codec.http.{HttpRequest, QueryStringDecoder}
import org.jboss.netty.buffer.ChannelBufferInputStream

trait HttpNettyRequest {

  /**
   * The wrapped Http Netty Request
   */
  val request: HttpRequest

  /**
   * Converts this request into a scalaz request.
   */
  def asRequest[I[_]](implicit in: InputStreamer[I]) = {

      val queryStringDecoder = new QueryStringDecoder(request.getUri)
      val path = queryStringDecoder.getPath
      val queryString = if(path == request.getUri) None else Some(request.getUri.substring(path.length + 1))
      val inputStream = new ChannelBufferInputStream(request.getContent)

    /* staying close to original impl. perhaps not the best way but its a start ... */

      val headers: List[(RequestHeader, NonEmptyList[Char])] = request.getHeaderNames.asScala.toList ∗
              (h => request.getHeaders(h).asScala.toList.filter(_.length > 0).map
                        (v => ((h: Option[RequestHeader]).get, v.toList.toNel.get)).toList)

      val rline = (request.getMethod.getName.toList: Option[Method]) >>= (m =>
        path.toList.toNel map
                (p => uri(p, queryString map (_.toList))) >>=
                (u => (request.getProtocolVersion.getText: Option[Version]) map
                        (v => line(m, u, v))))

      rline map (Request.request[I](_, headers, in(inputStream)))
    }
}

trait HttpNettyRequests {
  implicit def HttpNettyRequestRequest(r: HttpRequest): HttpNettyRequest = new HttpNettyRequest {
    val request = r
  }

  implicit def RequestHttpNettyRequest(request: HttpNettyRequest) = request.request
}

object HttpNettyRequest extends HttpNettyRequests