package com.pawelzabczynski

import com.pawelzabczynski.config.{Config, ConfigModule}
import com.softwaremill.quicklens._

package object test {
  val DefaultConfig: Config = new ConfigModule {}.config
  val TestConfig: Config = DefaultConfig
}
