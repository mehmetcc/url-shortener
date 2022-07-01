package infra

import zio.{Random, Task, ZIO, ZLayer}

trait Generator {
  def get: Task[String]
}

object Generator {
  def get: ZIO[Generator, Throwable, String] = ZIO.serviceWithZIO[Generator](_.get)
}

case class GeneratorLive(random: Random, config: LinkConfig) extends Generator {
  override def get: Task[String] = random.nextString(config.length)
}

object GeneratorLive {
  val layer: ZLayer[Random with Configuration, Throwable, GeneratorLive] = {
    ZLayer {
      for {
        config <- Configuration.load
        random <- ZIO.service[Random]
      } yield GeneratorLive(random, config.link)
    }
  }
}
