package com.pawelzabczynski.infrastructure

import com.pawelzabczynski.commons.models.Sensitive

case class DBConfig(username: String, password: Sensitive, url: String, migrateOnStart: Boolean, driver: String, connectThreadPoolSize: Int)
