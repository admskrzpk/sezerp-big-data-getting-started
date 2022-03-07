package com.pawelzabczynski.config

import com.pawelzabczynski.storm.spout.KafkaSpoutDeviceConfig

case class KafkaUploaderConfig(name: String, kafkaSpoutDevice: KafkaSpoutDeviceConfig)
