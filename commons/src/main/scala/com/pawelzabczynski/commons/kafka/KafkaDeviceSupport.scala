package com.pawelzabczynski.commons.kafka

import com.pawelzabczynski.commons.json.JsonSupport._
import com.pawelzabczynski.commons.models.web.{Device, DeviceMessage}
import org.apache.kafka.common.serialization.{Deserializer => KafkaDeserializer, Serializer => KafkaSerializer}
import com.pawelzabczynski.commons.models.KafkaMessages.KafkaMessage
import io.circe.syntax._
import monix.kafka.Serializer

import java.nio.charset.StandardCharsets
import java.util

trait KafkaDeviceSupport {
  implicit val deviceKafkaSerializerInstance: KafkaSerializer[KafkaMessage[Device]]     = new DeviceMessageSerializer()
  implicit val deviceKafkaDeserializerInstance: KafkaDeserializer[KafkaMessage[Device]] = new DeviceMessageDeserializer()
  implicit val deviceKafkaMessageSerializer: Serializer[KafkaMessage[Device]] = Serializer(
    className = "com.pawelzabczynski.commons.kafka.DeviceMessageSerializer",
    classType = classOf[DeviceMessageSerializer],
    constructor = (_: Serializer[KafkaMessage[Device]]) => deviceKafkaSerializerInstance
  )
}

final class DeviceMessageSerializer extends KafkaSerializer[KafkaMessage[Device]] {
  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {
    // nothing to do
  }

  override def serialize(topic: String, data: KafkaMessage[Device]): Array[Byte] = {
    data match {
      case msg: DeviceMessage =>
        val serializedToStr: String = noNullsPrinter.print(msg.asJson)
        serializedToStr.getBytes(StandardCharsets.UTF_8) // charset used by circe.io
    }
  }

  override def close(): Unit = {
    // nothing to do
  }
}

final class DeviceMessageDeserializer extends KafkaDeserializer[KafkaMessage[Device]] {
  override def deserialize(topic: String, data: Array[Byte]): KafkaMessage[Device] = {
    val str = new String(data, StandardCharsets.UTF_8)
    io.circe.parser.parse(str) match {
      case Right(msg) =>
        msg.as[KafkaMessage[Device]] match {
          case Right(dm) => dm
          case Left(de) => throw new RuntimeException(de)
        }
      case Left(pf) => throw new RuntimeException(pf)
    }
  }
}