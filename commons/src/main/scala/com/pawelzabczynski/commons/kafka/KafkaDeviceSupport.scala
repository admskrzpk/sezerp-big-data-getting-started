package com.pawelzabczynski.commons.kafka

import com.pawelzabczynski.commons.json.JsonSupport._
import com.pawelzabczynski.commons.models.web.{Device, DeviceMessage}
import org.apache.kafka.common.serialization.{Deserializer => KafkaDeserializer, Serializer => KafkaSerializer}
import io.circe.syntax._
import monix.kafka.{Deserializer, Serializer}

import java.nio.charset.StandardCharsets
import java.util

trait KafkaDeviceSupport {
  implicit val deviceKafkaSerializerInstance: KafkaSerializer[DeviceMessage]     = new DeviceMessageSerializer()
  implicit val deviceKafkaDeserializerInstance: KafkaDeserializer[DeviceMessage] = new DeviceMessageDeserializer()
  implicit val deviceKafkaMessageSerializer: Serializer[DeviceMessage] = Serializer(
    className = "com.pawelzabczynski.commons.kafka.DeviceMessageSerializer",
    classType = classOf[DeviceMessageSerializer],
    constructor = (_: Serializer[DeviceMessage]) => deviceKafkaSerializerInstance
  )
  implicit val deviceKafkaMessageDeserializer: Deserializer[DeviceMessage] = Deserializer(
    className = "com.pawelzabczynski.commons.kafka.DeviceMessageDeserializer",
    classType = classOf[DeviceMessageDeserializer],
    constructor = (_: Deserializer[DeviceMessage]) => deviceKafkaDeserializerInstance
  )
}

final class DeviceMessageSerializer extends KafkaSerializer[DeviceMessage] {
  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {
    // nothing to do
  }

  override def serialize(topic: String, data: DeviceMessage): Array[Byte] = {
        val serializedToStr: String = noNullsPrinter.print(data.asJson)
        serializedToStr.getBytes(StandardCharsets.UTF_8) // charset used by circe.io
  }

  override def close(): Unit = {
    // nothing to do
  }
}

final class DeviceMessageDeserializer extends KafkaDeserializer[DeviceMessage] {
  override def deserialize(topic: String, data: Array[Byte]): DeviceMessage = {
    val str = new String(data, StandardCharsets.UTF_8)
    io.circe.parser.parse(str) match {
      case Right(msg) =>
        msg.as[DeviceMessage] match {
          case Right(dm) => dm
          case Left(de) => throw new RuntimeException(de)
        }
      case Left(pf) => throw new RuntimeException(pf)
    }
  }
}