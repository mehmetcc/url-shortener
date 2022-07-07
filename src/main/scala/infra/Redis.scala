package infra

import com.redis.RedisClientPool
import infra.Pair.Key
import zio.{IO, Task, ZIO, ZLayer}

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object Pair {
  type Key   = String
  type Value = String
}

trait Redis {
  def set(key: Pair.Key, value: Pair.Value): Task[Pair.Key]

  def get(key: Pair.Key): IO[Option[Nothing], String]

  def exists(key: Pair.Key): Task[Boolean]
}

object Redis {
  def set(key: Pair.Key, value: Pair.Value): ZIO[Redis, Throwable, Key] = ZIO.serviceWithZIO[Redis](_.set(key, value))

  def get(key: Pair.Key): ZIO[Redis, Option[Nothing], String] = ZIO.serviceWithZIO[Redis](_.get(key))

  def exists(key: Pair.Key): ZIO[Redis, Throwable, Boolean] = ZIO.serviceWithZIO[Redis](_.exists(key))
}

case class RedisLive(pool: RedisClientPool, config: RedisConfig) extends Redis {
  override def set(key: Pair.Key, value: Pair.Value): Task[Pair.Key] = ZIO.attempt {
    pool.withClient { client =>
      client.set(key = key, value = value, expire = config.expire hours)
    }
  }.as(key)

  override def get(key: Pair.Key): IO[Option[Nothing], String] = ZIO.fromOption {
    pool.withClient { client =>
      client.get(key)
    }
  }

  override def exists(key: Key): Task[Boolean] = ZIO.attempt {
    pool.withClient { client =>
      client.exists(key)
    }
  }
}

object RedisLive {
  val layer: ZLayer[Configuration, Throwable, RedisLive] = ZLayer {
    for {
      config <- Configuration.load
      pool   <- makePool(config.redis.host, config.redis.port)
    } yield RedisLive(pool, config.redis)
  }

  private def makePool(host: String, port: Int): Task[RedisClientPool] = ZIO.attempt {
    new RedisClientPool(
      host = host,
      port = port
    )
  }
}
