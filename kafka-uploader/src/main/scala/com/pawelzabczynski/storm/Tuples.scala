package com.pawelzabczynski.storm

import org.apache.storm.tuple.Fields

object Tuples {

  val Topic     = "Topic"
  val Partition = "Partition"
  val Message   = "Value"

  val TopicIdx     = 0
  val PartitionIdx = 1
  val MessageIdx   = 2

  val defaultFields = new Fields(Topic, Partition, Message)


}
