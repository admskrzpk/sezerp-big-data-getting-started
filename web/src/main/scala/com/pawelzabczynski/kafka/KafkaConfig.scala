package com.pawelzabczynski.kafka

import scala.concurrent.duration.FiniteDuration

case class KafkaConfig(
    bootstrapServices: List[String],
    topic: String,
    batchSize: Int,
    clientId: String,
    acks: String,
    lingerMs: FiniteDuration
)
