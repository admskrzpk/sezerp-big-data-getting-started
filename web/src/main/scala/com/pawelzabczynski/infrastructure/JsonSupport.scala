package com.pawelzabczynski.infrastructure

import com.softwaremill.tagging.@@
import io.circe.{Decoder, Encoder, Printer}
import io.circe.generic.AutoDerivation
import com.pawelzabczynski.utils.Id

object JsonSupport  extends AutoDerivation {
  val noNullsPrinter: Printer = Printer.noSpaces.copy(dropNullValues = true)

  implicit def taggedIdEncoder[U]: Encoder[Id @@ U] = Encoder.encodeString.asInstanceOf[Encoder[Id @@ U]]
  implicit def taggedIdDecoder[U]: Decoder[Id @@ U] = Decoder.decodeString.asInstanceOf[Decoder[Id @@ U]]

  implicit def taggedStringEncoder[U]: Encoder[String @@ U] = Encoder.encodeString.asInstanceOf[Encoder[String @@ U]]
  implicit def taggedStringDecoder[U]: Decoder[String @@ U] = Decoder.decodeString.asInstanceOf[Decoder[String @@ U]]
}
