package example

object Example {

  import scalaz._
  import Scalaz._

  import http._
  import response._

  import netty.Netty._


  val application = Application.application[Stream, Stream](implicit req => {

    implicit val charset = UTF8

    OK(ContentType, "text/html") << transitional <<
      <html>
        <body>
          <p>Hello World</p>
        </body>
      </html>
  })
}

import java.util.concurrent.Executors
import java.net.InetSocketAddress

import org.jboss.netty._
import bootstrap.ServerBootstrap
import channel._
import group.DefaultChannelGroup
import socket.nio.NioServerSocketChannelFactory
import handler.codec.http._

object HttpServer {
  def main(args: Array[String]) {
    val g = new DefaultChannelGroup("scalaz")
    val f = new NioServerSocketChannelFactory(Executors.newCachedThreadPool, Executors.newCachedThreadPool)
    val bootstrap = new ServerBootstrap(f)
    bootstrap.setPipelineFactory(new HttpServerPipelineFactory)
    val channel = bootstrap.bind(new InetSocketAddress(8080))
    g.add(channel)

    Console.readLine
    g.close.awaitUninterruptibly
    f.releaseExternalResources
  }
}

class HttpServerPipelineFactory extends ChannelPipelineFactory {

  import scalaz.http.netty._

  def getPipeline = {
    val pipeline = Channels.pipeline
    pipeline.addLast("decoder", new HttpRequestDecoder)
    pipeline.addLast("encoder", new HttpResponseEncoder)
    pipeline.addLast("deflater", new HttpContentCompressor)
    pipeline.addLast("handler", ScalazHandler(NettyApplication.application(Example.application)))

    pipeline
  }
}