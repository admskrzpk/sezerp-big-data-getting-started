package com.pawelzabczynski.config

import com.pawelzabczynski.http.HttpConfig
import com.pawelzabczynski.infrastructure.DataBaseConfig

case class Config(webApp: HttpConfig, db: DataBaseConfig)
