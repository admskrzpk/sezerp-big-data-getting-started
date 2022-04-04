package com.pawelzabczynski.config

import com.pawelzabczynski.storm.{JdbcUploaderBoltConfig, KafkaSpoutDeviceConfig, UploaderTopologyConfig}

case class KafkaUploaderConfig(
    topology: UploaderTopologyConfig,
    kafkaSpoutDevice: KafkaSpoutDeviceConfig,
    jdbcUploaderBolt: JdbcUploaderBoltConfig
)
