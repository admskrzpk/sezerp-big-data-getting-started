package com.pawelzabczynski.storm.spout

import org.apache.storm.kafka.spout.KafkaSpout

object DeviceMessageSpout {

  val SpoutId = "kafka-uploader-device-spout"

  def createUnsafe(config: KafkaSpoutDeviceConfig): KafkaSpout[String, Array[Byte]] = {
    val configuration = KafkaSpoutDeviceConfig.createUnsafe(config)

    new KafkaSpout(configuration)
  }

}

