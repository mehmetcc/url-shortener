package infra

import pureconfig.ConfigSource
import pureconfig.generic.auto._
import zio.{Task, ULayer, ZIO, ZLayer}

case class AppConfig(server: ServerConfig, redis: RedisConfig, link: LinkConfig)

case class ServerConfig(host: String, port: Int)

case class RedisConfig(host: String, port: Int, expire: Int)

case class LinkConfig(length: Int)

trait Configuration {
  def load: Task[AppConfig]
}

object Configuration {
  def load: ZIO[Configuration, Throwable, AppConfig] = ZIO.serviceWithZIO[Configuration](_.load)
}

case class ConfigurationLive() extends Configuration {
  override def load: Task[AppConfig] = ZIO.fromEither {
    ConfigSource.default
      .load[AppConfig]
      .left
      .map(pureconfig.error.ConfigReaderException.apply)
  }
}

object ConfigurationLive {
  val layer: ULayer[ConfigurationLive] = ZLayer.succeed(ConfigurationLive())
}
