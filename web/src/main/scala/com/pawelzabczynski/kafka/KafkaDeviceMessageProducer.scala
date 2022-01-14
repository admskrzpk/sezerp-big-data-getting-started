package com.pawelzabczynski.kafka

import cats.effect.Resource
import cats.implicits.catsSyntaxApplicativeId
import com.pawelzabczynski.device.Device
import com.pawelzabczynski.kafka.KafkaMessages.KafkaMessage
import monix.eval.Task
import monix.execution.Scheduler.global
import monix.kafka.{KafkaProducer, KafkaProducerConfig}
import monix.kafka.Serializer._
import com.pawelzabczynski.kafka.KafkaSerializationSupport._
import monix.kafka.config.Acks
import org.apache.kafka.clients.producer.{ProducerRecord, RecordMetadata}
import org.apache.kafka.clients.producer.{KafkaProducer => ApacheKafkaProducer}

import scala.concurrent.duration.DurationInt

class KafkaDeviceMessageProducer(config: KafkaConfig) {
  val producerResources: Resource[Task, MessageProducer] = MessageProducer.apply(config)
}


final class MessageProducer(topic: String, producer: KafkaProducer[String, KafkaMessage[Device]]) {

  def underlying: Task[ApacheKafkaProducer[String, KafkaMessage[Device]]] = {
    producer.underlying
  }

  def send(value: KafkaMessage[Device]): Task[Option[RecordMetadata]] = {
    producer.send(topic, value)
  }

  def send(key: String, value: KafkaMessage[Device]): Task[Option[RecordMetadata]] = {
    producer.send(topic, key, value)
  }

  def send(record: ProducerRecord[String, KafkaMessage[Device]]): Task[Option[RecordMetadata]] = {
    producer.send(record)
  }

    def close(): Task[Unit] = {
      producer.close()
    }
}

object MessageProducer {
  def apply(config: KafkaConfig): Resource[Task, MessageProducer] = {
    Resource.make {
      createUnsafe(config).pure[Task]
    } { producer =>
      producer.close()
    }
  }

  def createUnsafe(config: KafkaConfig): MessageProducer = {
    val producerConf = KafkaProducerConfig.default.copy(
      bootstrapServers = config.bootstrapServices,
      batchSizeInBytes = config.batchSize,
      clientId = config.clientId,
      acks = Acks(config.acks),
      lingerTime = config.lingerMs,
      connectionsMaxIdleTime = 20.minutes
    )

    val producer = KafkaProducer[String, KafkaMessage[Device]](producerConf, global)

    new MessageProducer(config.topic, producer)
  }
}


