package com.pawelzabczynski.storm.spout

import com.pawelzabczynski.kafka.KafkaConfig
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.storm.kafka.spout.{ByTopicRecordTranslator, KafkaSpoutConfig}
import org.apache.storm.tuple.{Fields, Values}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

case class KafkaSpoutDeviceConfig(kafka: KafkaConfig)

object KafkaSpoutDeviceConfig {

  object KafkaSpoutFields {
    val Topic     = "Topic"
    val Partition = "Partition"
    val Message   = "Value"
  }

  def createUnsafe(config: KafkaSpoutDeviceConfig): KafkaSpoutConfig[String, Array[Byte]] = {
    val translator = new ByTopicRecordTranslator[String, Array[Byte]](
      (record: ConsumerRecord[String, Array[Byte]]) => {

        println(s"Received message: ${record.value()}")
        new Values(record.topic(), record.partition(), record.value())
      },
      new Fields(KafkaSpoutFields.Topic, KafkaSpoutFields.Partition, KafkaSpoutFields.Message)
    )

    new KafkaSpoutConfig.Builder[String, Array[Byte]](config.kafka.consumer.bootstrapServers.mkString(","), config.kafka.consumer.topic)
      .setRecordTranslator(translator)
      .setProp(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer])
      .setProp(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[ByteArrayDeserializer])
      .setProp(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 10)
      .setProp(ConsumerConfig.GROUP_ID_CONFIG, config.kafka.consumer.groupId)
      .build()
  }
}
