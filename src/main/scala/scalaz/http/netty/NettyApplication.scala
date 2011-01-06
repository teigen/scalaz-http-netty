package scalaz
package http
package netty

import request.Request
import response.Response
import org.jboss.netty.channel.{SimpleChannelUpstreamHandler, ChannelHandlerContext}
import org.jboss.netty.handler.codec.http.HttpRequest

abstract class NettyApplication[IN[_], OUT[_]](implicit i: InputStreamer[IN], e: Each[OUT]) {
  /**
   * Returns a response for the given application parts.
   */
  def application(implicit handler: SimpleChannelUpstreamHandler, ctx: ChannelHandlerContext, e: HttpRequest, request: Request[IN]) : Response[OUT]

  /**
   * Returns a web application for the given netty application parts.
   */
  def application(implicit handler: SimpleChannelUpstreamHandler, ctx: ChannelHandlerContext, r: HttpRequest) : Application[IN, OUT] = new Application[IN, OUT] {
    def apply(implicit request: Request[IN]) = application(handler, ctx, r, request)
  }

  /**
   * Returns a response for the given application parts.
   */
  def apply(handler: SimpleChannelUpstreamHandler, ctx: ChannelHandlerContext, r: HttpRequest, req: Request[IN]) = application(handler, ctx, r)(req)

  /**
   * The input-streamer for the contents of the request body.
   */
  val inputStreamer = i

  /**
   * The each for the contents of the response body.
   */
  val each = e
}

object NettyApplication {
  /**
   * Construct a netty application from the given function.
   */
  def nettyApplication[IN[_], OUT[_]](f: (SimpleChannelUpstreamHandler, ChannelHandlerContext, HttpRequest, Request[IN]) => Response[OUT])(implicit i: InputStreamer[IN], e: Each[OUT]): NettyApplication[IN, OUT] =
    new NettyApplication[IN, OUT] {
      def application(implicit handler: SimpleChannelUpstreamHandler, ctx: ChannelHandlerContext, r: HttpRequest, request: Request[IN]) = f(handler, ctx, r, request)
    }

  /**
   * Construct a netty application from the given function.
   */
  def nettyApplication_[IN[_], OUT[_]](f: (SimpleChannelUpstreamHandler, ChannelHandlerContext, HttpRequest) => Application[IN, OUT])(implicit i: InputStreamer[IN], e: Each[OUT]): NettyApplication[IN, OUT] =
    new NettyApplication[IN, OUT] {
      def application(implicit handler: SimpleChannelUpstreamHandler, ctx: ChannelHandlerContext, r: HttpRequest, request: Request[IN]) = f(handler, ctx, r)(request)
    }

  /**
   * Construct a netty application from the given function.
   */
  def nettyApplication__[IN[_], OUT[_]](f: (SimpleChannelUpstreamHandler) => Application[IN, OUT])(implicit i: InputStreamer[IN], e: Each[OUT]) =
    nettyApplication_[IN, OUT]((h, c, r) => f(h))

  /**
   * Construct a netty application from the given function.
   */
  def nettyApplication___[IN[_], OUT[_]](f: (ChannelHandlerContext, HttpRequest) => Application[IN, OUT])(implicit i: InputStreamer[IN], e: Each[OUT]) =
    nettyApplication_[IN, OUT]((h, c, r) => f(c, r))

  /**
   * Construct a netty application from the given function.
   */
  def nettyApplication____[IN[_], OUT[_]](f: (HttpRequest) => Application[IN, OUT])(implicit i: InputStreamer[IN], e: Each[OUT]) =
    nettyApplication_[IN, OUT]((h, c, r) => f(r))

  /**
   * Construct a netty application from the given constant value.
   */
  def application[IN[_], OUT[_]](a: Application[IN, OUT])(implicit i: InputStreamer[IN], e: Each[OUT]) =
    nettyApplication_[IN, OUT]((h, c, r) => a)
}