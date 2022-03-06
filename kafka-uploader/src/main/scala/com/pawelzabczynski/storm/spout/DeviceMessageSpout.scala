package com.pawelzabczynski.storm.spout

import com.pawelzabczynski.commons.models.web.{Device, DeviceMessage}
import org.apache.kafka.common.TopicPartition
import org.apache.storm.kafka.spout.KafkaSpout
import org.apache.storm.kafka.spout.subscription.ManualPartitioner
import org.apache.storm.task.TopologyContext

import java.util

class DeviceMessageSpout() extends ManualPartitioner {
  override def getPartitionsForThisTask(allPartitionsSorted: util.List[TopicPartition], context: TopologyContext): util.Set[TopicPartition] = ???
}

object DeviceMessageSpout {

}

