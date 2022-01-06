package com.pawelzabczynski.infrastructure

import com.pawelzabczynski.config.Sensitive

case class DataBaseConfig(username: String, password: Sensitive, url: String, driver: String, connectionThreadPoolSize: Int)
