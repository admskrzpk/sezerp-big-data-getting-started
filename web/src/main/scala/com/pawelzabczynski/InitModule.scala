package com.pawelzabczynski

import com.pawelzabczynski.config.ConfigModule
import com.pawelzabczynski.infrastructure.DataBase
import com.pawelzabczynski.kafka.KafkaDeviceMessageProducer

trait InitModule extends ConfigModule {

  lazy val dataBase      = new DataBase(config.db)
  lazy val kafkaProducer = new KafkaDeviceMessageProducer(config.kafka)

}
