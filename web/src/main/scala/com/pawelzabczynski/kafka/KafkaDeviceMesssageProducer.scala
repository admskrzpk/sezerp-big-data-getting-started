package com.pawelzabczynski.kafka

import cats.effect.Resource
import com.pawelzabczynski.device.Device
import com.pawelzabczynski.kafka.KafkaMessages.KafkaMessage
import monix.eval.Task
import monix.execution.Scheduler.global
import monix.kafka.{KafkaProducer, KafkaProducerConfig}
import monix.kafka.Serializer._
import com.pawelzabczynski.kafka.KafkaSerializationSupport._

class KafkaDeviceMesssageProducer(config: KafkaConfig) {
  val producerResources: Resource[Task, KafkaProducer[String, KafkaMessage[Device]]] = {
    Resource.make {
      Task {
        val producerConf = KafkaProducerConfig.default.copy(bootstrapServers = config.bootstrapServices)

        KafkaProducer[String, KafkaMessage[Device]](producerConf, global)
      }
    } { producer =>
      producer.close()
    }
  }
}
