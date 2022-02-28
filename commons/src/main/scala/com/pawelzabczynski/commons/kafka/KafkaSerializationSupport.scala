package com.pawelzabczynski.commons.kafka

import com.pawelzabczynski.commons.models.web.Device
import com.pawelzabczynski.commons.models.web.DeviceMessage
import com.pawelzabczynski.commons.models.KafkaMessages
import monix.kafka.Serializer
import org.apache.kafka.common.serialization.{Deserializer => KafkaDeserializer, Serializer => KafkaSerializer}
import com.pawelzabczynski.commons.json.JsonSupport._
import io.circe.syntax._

import java.nio.charset.StandardCharsets
import java.util

object KafkaSerializationSupport extends KafkaDeviceSupport {}
