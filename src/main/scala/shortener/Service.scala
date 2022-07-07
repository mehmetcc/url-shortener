package shortener

import infra.Pair.{Key, Value}
import infra.{Generator, Pair, Redis}
import zio.{Task, ZIO, ZLayer}

import java.nio.charset.StandardCharsets

trait Service {
  def createLink(target: Pair.Value): Task[Pair.Key]

  def retrieveLink(key: Pair.Value): Task[Pair.Value]
}

object Service {
  def createLink(target: Pair.Value): ZIO[Service, Throwable, Key] =
    ZIO.serviceWithZIO[Service](_.createLink(target))

  def retrieveLink(key: Pair.Value): ZIO[Service, Throwable, Value] =
    ZIO.serviceWithZIO[Service](_.retrieveLink(key))
}

case class ServiceLive(redis: Redis, generator: Generator) extends Service {
  override def createLink(target: Value): Task[Key] = for {
    key       <- generateKey
    _         <- ZIO.log(s"Generated key: ${key}")
    persisted <- redis.set(key, target)
  } yield persisted

  private def generateKey: Task[Key] = for {
    generated <- generator.get
    exists    <- redis.exists(generated)
    key       <- if (exists) generateKey else ZIO.succeed(generated)
  } yield key

  override def retrieveLink(key: Value): Task[Pair.Value] =
    redis.get(key).orElseFail(new Throwable("Given key does not exist!"))
}

object ServiceLive {
  val layer: ZLayer[Generator with Redis, Nothing, ServiceLive] = ZLayer {
    for {
      redis     <- ZIO.service[Redis]
      generator <- ZIO.service[Generator]
    } yield ServiceLive(redis, generator)
  }
}
