package com.pawelzabczynski.kafka

case class KafkaConfig(consumer: KafkaConsumerConfig)

case class KafkaConsumerConfig(bootstrapServers: List[String], topic: String, groupId: String, fetchMinBytes: Int, fetchMaxBytes: Int, partitionsNumber: Int)
