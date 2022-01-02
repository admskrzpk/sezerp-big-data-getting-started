package com.pawelzabczynski.config

import com.typesafe.scalalogging.StrictLogging
import pureconfig.ConfigSource

import pureconfig.generic.auto._

trait ConfigModule extends StrictLogging {
  lazy val config: Config = ConfigSource.default.loadOrThrow[Config]

  def logConfiguration(): Unit = {
    val baseInfo =
      s"""
         |Web app configuration:
         |------------------------
         |host: ${config.webApp.host}
         |port: ${config.webApp.port}
         |""".stripMargin

    logger.info(baseInfo)
  }

}
