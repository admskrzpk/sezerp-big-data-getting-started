package com.pawelzabczynski.infrastructure

import com.pawelzabczynski.device.Device
import com.pawelzabczynski.kafka.KafkaMessages.{DeviceMessage, KafkaMessage}
import com.softwaremill.tagging.@@
import io.circe.{Decoder, Encoder, Printer}
import io.circe.generic.AutoDerivation
import com.pawelzabczynski.utils.Id
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt
import io.circe.{Decoder, Encoder}
import io.circe.generic.auto._
import io.circe.syntax._
import cats.syntax.functor._

object JsonSupport extends AutoDerivation {
  val noNullsPrinter: Printer = Printer.noSpaces.copy(dropNullValues = true)

  implicit val passwordHashEncoder: Encoder[PasswordHash[SCrypt]] =
    Encoder.encodeString.asInstanceOf[Encoder[PasswordHash[SCrypt]]]

  implicit def taggedIdEncoder[U]: Encoder[Id @@ U] = Encoder.encodeString.asInstanceOf[Encoder[Id @@ U]]
  implicit def taggedIdDecoder[U]: Decoder[Id @@ U] = Decoder.decodeString.asInstanceOf[Decoder[Id @@ U]]

  implicit def taggedStringEncoder[U]: Encoder[String @@ U] = Encoder.encodeString.asInstanceOf[Encoder[String @@ U]]
  implicit def taggedStringDecoder[U]: Decoder[String @@ U] = Decoder.decodeString.asInstanceOf[Decoder[String @@ U]]

  implicit val encodeKafkaMessage: Encoder[KafkaMessage[Device]] = Encoder.instance {
    case dm: DeviceMessage => dm.asJson
  }
  implicit val decodeKafkaMessage: Decoder[KafkaMessage[Device]] = List[Decoder[KafkaMessage[Device]]](
    Decoder[DeviceMessage].widen
  ).reduceLeft(_ or _)
}
