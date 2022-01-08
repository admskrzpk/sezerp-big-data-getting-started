package com.pawelzabczynski.test

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import com.pawelzabczynski.config.Sensitive
import com.pawelzabczynski.infrastructure.DataBaseConfig
import com.typesafe.scalalogging.StrictLogging
import org.postgresql.jdbc.PgConnection
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite}

trait TestEmbeddedPostgres extends BeforeAndAfterEach with BeforeAndAfterAll with StrictLogging { self: Suite =>
  private var postgres: EmbeddedPostgres = _
  private var currentDbConfig: DataBaseConfig  = _
  var currentDb: TestDataBase                  = _

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    postgres = EmbeddedPostgres.builder().start()
    val url = postgres.getJdbcUrl("postgres", "postgres")
    postgres.getPostgresDatabase.getConnection.asInstanceOf[PgConnection].setPrepareThreshold(100)
    currentDbConfig = TestConfig.db.copy(
      username = "postgres",
      password = Sensitive(""),
      url = url
    )
    currentDb = new TestDataBase(currentDbConfig)
    currentDb.connectAndMigrate()
  }

  override protected def afterAll(): Unit = {
    postgres.close()
    currentDb.close()
    super.afterAll()
  }

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    currentDb.migrate()
  }

  override protected def afterEach(): Unit = {
    currentDb.clean()
    super.afterEach()
  }
}
