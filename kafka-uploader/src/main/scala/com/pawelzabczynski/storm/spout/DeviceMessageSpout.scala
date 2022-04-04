package com.pawelzabczynski.storm.spout

import com.pawelzabczynski.storm.{KafkaSpoutDeviceConfig, Tuples}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import org.apache.storm.kafka.spout.{ByTopicRecordTranslator, KafkaSpout, KafkaSpoutConfig}
import org.apache.storm.tuple.Values

object DeviceMessageSpout {

  val SpoutId = "kafka-uploader-device-spout"

  def createUnsafe(config: KafkaSpoutDeviceConfig): KafkaSpout[String, Array[Byte]] = {
    val translator = new ByTopicRecordTranslator[String, Array[Byte]](
      (record: ConsumerRecord[String, Array[Byte]]) => {
        new Values(record.topic(), record.partition(), record.value())
      },
      Tuples.defaultFields
    )

    val configuration = new KafkaSpoutConfig.Builder[String, Array[Byte]](config.kafka.consumer.bootstrapServers.mkString(","), config.kafka.consumer.topic)
      .setRecordTranslator(translator)
      .setProp(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer])
      .setProp(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[ByteArrayDeserializer])
      .setProp(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 10)
      .setProp(ConsumerConfig.GROUP_ID_CONFIG, config.kafka.consumer.groupId)
      .build()

    new KafkaSpout(configuration)
  }

}

