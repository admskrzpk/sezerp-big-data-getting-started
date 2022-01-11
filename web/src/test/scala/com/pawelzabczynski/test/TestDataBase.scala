package com.pawelzabczynski.test

import cats.effect.{Blocker, ContextShift}
import com.pawelzabczynski.infrastructure.DataBaseConfig
import com.typesafe.scalalogging.StrictLogging
import doobie.Transactor
import doobie.hikari.HikariTransactor
import doobie.implicits.toSqlInterpolator
import monix.catnap.MVar
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.flywaydb.core.Flyway
import com.pawelzabczynski.infrastructure.Doobie._

import scala.annotation.tailrec
import scala.concurrent.duration._

class TestDataBase(config: DataBaseConfig) extends StrictLogging {
  var xa: Transactor[Task]                          = _
  private val xaReady: MVar[Task, Transactor[Task]] = MVar.empty[Task, Transactor[Task]]().runSyncUnsafe()
  private val done: MVar[Task, Unit]                = MVar.empty[Task, Unit]().runSyncUnsafe()

  {
    implicit val contextShift: ContextShift[Task] = Task.contextShift(monix.execution.Scheduler.global)

    val xaResource = for {
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
    } yield xa

    xaResource
      .use { _xa =>
        xaReady.put(_xa) >> done.take
      }
      .startAndForget
      .runSyncUnsafe()

    xa = xaReady.take.runSyncUnsafe()
  }

  private val flyway = {
    Flyway
      .configure()
      .locations("filesystem:./migrations/db")
      .dataSource(config.url, config.username, config.password.value)
      .load()
  }

  @tailrec
  final def connectAndMigrate(): Unit = {
    try {
      migrate()
      testConnection()
      logger.info("Database migration & connection test complete")
    } catch {
      case e: Exception =>
        logger.warn("Database not available, waiting 5 seconds to retry...", e)
        Thread.sleep(5000)
        connectAndMigrate()
    }
  }

  def migrate(): Unit = {
    flyway.migrate()
    ()
  }

  def clean(): Unit = {
    flyway.clean()
  }

  def testConnection(): Unit = {
    sql"select 1".query[Int].unique.transact(xa).void.runSyncUnsafe(1.minute)
  }

  def close(): Unit = {
    done.put(()).runSyncUnsafe(1.minute)
  }
}
