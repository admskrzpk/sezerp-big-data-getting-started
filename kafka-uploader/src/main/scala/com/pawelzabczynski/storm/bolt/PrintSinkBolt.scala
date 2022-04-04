package com.pawelzabczynski.storm.bolt

import com.typesafe.scalalogging.LazyLogging
import org.apache.storm.topology.{BasicOutputCollector, OutputFieldsDeclarer}
import org.apache.storm.topology.base.BaseBasicBolt
import org.apache.storm.tuple.Tuple
import com.pawelzabczynski.commons.json.JsonSupport._
import com.pawelzabczynski.commons.models.web.DeviceMessage
import com.pawelzabczynski.storm.Tuples

import java.nio.charset.StandardCharsets

private class PrintSinkBolt extends BaseBasicBolt with LazyLogging {
  override def execute(input: Tuple, collector: BasicOutputCollector): Unit = {
    input.getValueByField(Tuples.Message) match {
      case v: Array[Byte] =>
        val str = new String(v, StandardCharsets.UTF_8)
        io.circe.parser.parse(str) match {
          case Right(msg) =>
            msg.as[DeviceMessage] match {
              case Right(dm) =>
                logger.info(s"#############################################")
                logger.info(s"$dm")
              case Left(de) => logger.error(s"Cannot parse: $str", de)
            }
          case Left(pf) =>
            logger.error("Parsing failure", pf)
            logger.info(s"Cannot parse: $str")
        }
      case v => logger.info(s"$v")
    }
  }

  override def declareOutputFields(declarer: OutputFieldsDeclarer): Unit = {}
}

object PrintSinkBolt {
  val BoltId = "print-sink-bolt"
  def createUnsafe(): BaseBasicBolt = {
    new PrintSinkBolt()
  }
}
