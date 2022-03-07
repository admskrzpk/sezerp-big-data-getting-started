package com.pawelzabczynski.kafka

import cats.effect.Resource
import monix.kafka.KafkaConsumerObservable
import com.pawelzabczynski.commons.models.web.Device
import com.pawelzabczynski.commons.models.KafkaMessages.KafkaMessage
import monix.eval.Task
import org.apache.kafka.clients.consumer.ConsumerRecord
import monix.kafka.{KafkaConsumerConfig => MonixConsumerConfig}
import com.pawelzabczynski.commons.kafka.KafkaSerializationSupport._

object KafkaConsumer {
  def create(kafkaConfig: KafkaConfig): Resource[Task, KafkaConsumerObservable[String, KafkaMessage[Device], ConsumerRecord[String, KafkaMessage[Device]]]] = {
    Resource.make {
        Task {
          val consumerCfg = MonixConsumerConfig.default

          KafkaConsumerObservable.apply[String, KafkaMessage[Device]](consumerCfg, List("my-topic"))
        }
    } { _ =>
      Task(())
    }
  }
}