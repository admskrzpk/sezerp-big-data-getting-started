package com.pawelzabczynski

import com.pawelzabczynski.storm.UploaderTopology
import com.typesafe.scalalogging.LazyLogging
import org.apache.storm.{Config, LocalCluster, StormSubmitter}

import scala.util.Using

object KafkaUploaderApp extends LazyLogging {

  def main(args: Array[String]): Unit = {
    val mainModule = new MainModule {}
    mainModule.logConfiguration()

    Using(new LocalCluster()) { _ =>
      val topology = UploaderTopology.createUnsafe(mainModule.config)
      val conf = new Config();
      conf.setDebug(false)
      println("Starting topology...")
      StormSubmitter.submitTopology(mainModule.config.kafkaUploader.name, conf, topology)
      println("Topology started")
    }

  }

}
