package com.pawelzabczynski.infrastructure

import cats.effect.{Blocker, Resource}
import com.typesafe.scalalogging.StrictLogging
import doobie.Transactor
import doobie.hikari.HikariTransactor
import monix.eval.Task
import Doobie._

import scala.concurrent.duration.DurationInt

class DataBase(config: DataBaseConfig) extends StrictLogging {

  val txResource: Resource[Task, Transactor[Task]] = {
    for {
      connectEC  <- doobie.util.ExecutionContexts.fixedThreadPool[Task](config.connectionThreadPoolSize)
      transactEC <- doobie.util.ExecutionContexts.cachedThreadPool[Task]
      xa <- HikariTransactor.newHikariTransactor[Task](
        config.driver,
        config.url,
        config.username,
        config.password.value,
        connectEC,
        Blocker.liftExecutionContext(transactEC)
      )
      _ <- Resource.eval(testConnection(xa))
    } yield xa
  }

  private def testConnection(xa: Transactor[Task]): Task[Unit] =
    (sql"select 1".query[Int].unique.transact(xa).void >> Task(logger.info("Database connection test complete")))
      .onErrorRecoverWith { case e: Exception =>
        logger.warn("Database not available, waiting 5 seconds to retry...", e)
        Task.sleep(5.seconds) >> testConnection(xa)
      }
}
