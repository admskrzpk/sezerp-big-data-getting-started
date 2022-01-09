package com.pawelzabczynski.kafka

import com.pawelzabczynski.device.Device
import com.pawelzabczynski.kafka.KafkaMessages.{DeviceMessage, KafkaMessage}
import monix.kafka.Serializer
import org.apache.kafka.common.serialization.{Serializer => KafkaSerializer}
import com.pawelzabczynski.infrastructure.JsonSupport._
import io.circe.syntax._

import java.nio.charset.StandardCharsets
import java.util

object KafkaSerializationSupport {
  lazy val  deviceKafkaSerializerInstance: KafkaSerializer[KafkaMessage[Device]] = new DeviceMessageSerializer()
  implicit val deviceKafkaMessageSerializer: Serializer[KafkaMessage[Device]] = Serializer(
  className = "com.pawelzabczynski.kafka.DeviceMessageSerializer",
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
        serializedToStr.getBytes(StandardCharsets.UTF_8)
    }
  }

  override def close(): Unit = {
    // nothing to do
  }
}
