package com.pawelzabczynski.storm

import com.pawelzabczynski.config.Config
import com.pawelzabczynski.storm.bolt.PrintSinkBolt
import com.pawelzabczynski.storm.spout.DeviceMessageSpout
import org.apache.storm.generated.StormTopology
import org.apache.storm.topology.TopologyBuilder

object UploaderTopology {
  def createUnsafe(config: Config): StormTopology = {
    val builder = new TopologyBuilder()

    val deviceSpout = DeviceMessageSpout.createUnsafe(config.kafkaUploader.kafkaSpoutDevice)
    builder.setSpout(DeviceMessageSpout.SpoutId, deviceSpout, config.kafkaUploader.kafkaSpoutDevice.kafka.consumer.partitionsNumber)

    val printBolt = PrintSinkBolt.createUnsafe()
    builder
      .setBolt(PrintSinkBolt.BoltId, printBolt, 1)
      .shuffleGrouping(DeviceMessageSpout.SpoutId)

    builder.createTopology()
  }
}
