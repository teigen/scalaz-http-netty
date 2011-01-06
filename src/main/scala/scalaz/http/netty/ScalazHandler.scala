package scalaz
package http
package netty

import HttpNettyRequest._
import HttpNettyResponse._

import org.jboss.netty.handler.codec.http.{HttpResponseStatus, HttpVersion, DefaultHttpResponse, HttpRequest}
import org.jboss.netty.channel.{ChannelFutureListener, MessageEvent, ChannelHandlerContext, SimpleChannelUpstreamHandler}

class ScalazHandler[IN[_], OUT[_]](application: NettyApplication[IN, OUT])(implicit in: InputStreamer[IN], each: Each[OUT]) extends SimpleChannelUpstreamHandler {

  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {

    val request = e.getMessage.asInstanceOf[HttpRequest]

    request.asRequest[IN].foreach(r => {
      val res = application.application(this, ctx, request, r)
      val response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
      response.respond[OUT](res)
      val future = e.getChannel.write(response)
      future.addListener(ChannelFutureListener.CLOSE)
    })
  }
}

object ScalazHandler {
  def apply[IN[_], OUT[_]](application: NettyApplication[IN, OUT])(implicit in: InputStreamer[IN], each: Each[OUT]):ScalazHandler[IN, OUT] =
    new ScalazHandler[IN, OUT](application)
}