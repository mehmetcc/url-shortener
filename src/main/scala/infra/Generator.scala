package infra

import zio.{Task, ZIO, ZLayer}

trait Generator {
  def get: Task[String]
}

object Generator {
  def get: ZIO[Generator, Throwable, String] = ZIO.serviceWithZIO[Generator](_.get)
}

case class GeneratorLive(random: scala.util.Random, config: LinkConfig) extends Generator {
  override def get: Task[String] = ZIO.attempt(nextString(config.length))

  private def nextString(length: Int): String = nextBytes(length).map(_.toChar).mkString

  private def nextBytes(length: Int): Array[Byte] = Seq.fill[Byte](length)(nextByte).toArray

  private def nextByte: Byte = random.shuffle(SeqConstants.ascii).head
}

object GeneratorLive {
  val layer: ZLayer[Configuration, Throwable, GeneratorLive] = {
    ZLayer {
      for {
        config <- Configuration.load
        random  = scala.util.Random
      } yield GeneratorLive(random, config.link)
    }
  }
}

private object SeqConstants {
  val ascii: Array[Byte] = Array[Int](
    97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120,
    121, 122, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90
  ).map(_.toByte)
}
