package com.pawelzabczynski.config

import com.pawelzabczynski.http.HttpConfig
import com.pawelzabczynski.infrastructure.DataBaseConfig
import com.pawelzabczynski.kafka.KafkaConfig

case class Config(webApp: HttpConfig, db: DataBaseConfig, kafka: KafkaConfig)
