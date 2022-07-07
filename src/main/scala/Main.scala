import infra.{Configuration, ConfigurationLive, GeneratorLive, RedisLive}
import zhttp.service.Server
import zio.{ZIO, ZLayer}

import scala.language.postfixOps
import scala.util.Random

object Main extends zio.ZIOAppDefault {
  private def startServer(port: Int) = Server
    .start(port = port, http = shortener.App())

  private def program = for {
    configuration <- Configuration.load
    port           = configuration.server.port
    server        <- startServer(port)
  } yield server

  override def run: ZIO[Any, Throwable, Nothing] = program
    .provide(
      ZLayer(ZIO.attempt(new Random)),
      ConfigurationLive.layer,
      GeneratorLive.layer,
      RedisLive.layer,
      shortener.ServiceLive.layer
    )
}
