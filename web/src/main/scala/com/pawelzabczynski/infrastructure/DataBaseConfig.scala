package com.pawelzabczynski.infrastructure

import com.pawelzabczynski.commons.models.Sensitive


case class DataBaseConfig(username: String, password: Sensitive, url: String, driver: String, connectionThreadPoolSize: Int)
