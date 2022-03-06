package com.pawelzabczynski.storm.spout

import com.pawelzabczynski.commons.models.web.DeviceMessage
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.storm.kafka.spout.{ByTopicRecordTranslator, KafkaSpoutConfig}
import org.apache.storm.tuple.{Fields, Values}
import org.apache.kafka.common.serialization.ByteArrayDeserializer

import scala.jdk.CollectionConverters._

case class KafkaSpoutDeviceConfig(groupId: String, topic: String, bootstrapServers: List[String])

object KafkaSpoutDeviceConfig {

  object KafkaSpoutFields {
    val Topic = "topic"
    val Partition = "Partition"
    val Value = "Value"
  }


  def createSpoutConfig(config: KafkaSpoutDeviceConfig): KafkaSpoutConfig[String, Array[Byte]] = {
    val translator = new ByTopicRecordTranslator[String, Array[Byte]](
      (record: ConsumerRecord[String, Array[Byte]]) => new Values(record.topic(), record.partition(), record.value()),
      new Fields(KafkaSpoutFields.Topic, KafkaSpoutFields.Partition, KafkaSpoutFields.Value)
    )

    new KafkaSpoutConfig.Builder[String, Array[Byte]](config.bootstrapServers.mkString(","), List(config.topic).toSet.asJava)
      .setRecordTranslator(translator)
      .setProp(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer")
      .build()
  }
}
