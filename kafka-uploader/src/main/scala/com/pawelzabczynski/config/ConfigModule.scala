package com.pawelzabczynski.config

import com.typesafe.scalalogging.StrictLogging
import pureconfig.ConfigSource
import pureconfig.generic.auto._

trait ConfigModule extends StrictLogging {
  lazy val config: Config = ConfigSource.default.loadOrThrow[Config]

  def logConfiguration(): Unit = {
    val baseInfo =
      s"""
         |Kafka app configuration:
         |------------------------
         |name: ${config.kafkaUploader.name}
         |""".stripMargin

    logger.info(baseInfo)
  }
}
