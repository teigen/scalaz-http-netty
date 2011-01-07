package scalaz
package http
package netty

import HttpNettyRequest._
import HttpNettyResponse._

import org.jboss.netty.channel.{ChannelFutureListener, MessageEvent, ChannelHandlerContext, SimpleChannelUpstreamHandler}
import org.jboss.netty.handler.codec.http._

class ScalazHandler[IN[_], OUT[_]](application: NettyApplication[IN, OUT]) extends SimpleChannelUpstreamHandler {

  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {

    val request = e.getMessage.asInstanceOf[HttpRequest]

    request.asRequest[IN](application.inputStreamer).foreach(r => {
      val res = application.application(this, ctx, request, r)
      val response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
      response.respond[OUT](res)(application.each)

      val keepAlive = HttpHeaders.isKeepAlive(request)

      if(keepAlive){
        response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, response.getContent.readableBytes)
      }

      val future = e.getChannel.write(response)

      if(!keepAlive){
        future.addListener(ChannelFutureListener.CLOSE)
      }
    })
  }
}

object ScalazHandler {
  def apply[IN[_], OUT[_]](application: NettyApplication[IN, OUT]):ScalazHandler[IN, OUT] =
    new ScalazHandler[IN, OUT](application)
}