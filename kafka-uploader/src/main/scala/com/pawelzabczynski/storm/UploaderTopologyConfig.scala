package com.pawelzabczynski.storm

import com.pawelzabczynski.commons.models.Sensitive

import scala.concurrent.duration.FiniteDuration

case class UploaderTopologyConfig(name: String)
case class KafkaConsumerConfig(
    bootstrapServers: List[String],
    topic: String,
    groupId: String,
    fetchMinBytes: Int,
    fetchMaxBytes: Int,
    partitionsNumber: Int
)
case class KafkaConfig(consumer: KafkaConsumerConfig)
case class KafkaSpoutDeviceConfig(kafka: KafkaConfig)
case class DbConfig(driver: String, url: String, username: String, password: Sensitive, connectionThreadPool: Int)
case class JdbcUploaderBoltConfig(timeout: FiniteDuration, batchSize: Int, table: String, db: DbConfig)
