package com.pawelzabczynski.kafka


case class KafkaUploaderConfig(name: String, kafka: KafkaConfig)

case class KafkaConfig(consumer: KafkaConsumerConfig)

case class KafkaConsumerConfig(brokerList: List[String], topic: String)
