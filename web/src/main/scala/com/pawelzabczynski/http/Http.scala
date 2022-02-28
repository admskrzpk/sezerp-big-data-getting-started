package com.pawelzabczynski.http

import cats.implicits.{catsSyntaxApplicativeError, catsSyntaxEitherId}
import com.pawelzabczynski.Fail
import com.pawelzabczynski.commons.models.Id
import com.typesafe.scalalogging.StrictLogging
import sttp.model.StatusCode
import sttp.tapir.{Codec, Endpoint, EndpointOutput, Schema, SchemaType, Tapir}
import sttp.tapir.json.circe.TapirJsonCirce
import com.pawelzabczynski.infrastructure.JsonSupport._
import com.softwaremill.tagging.@@
import io.circe.Printer
import monix.eval.Task
import sttp.tapir.Codec.PlainCodec
import tsec.common.SecureRandomId
import com.softwaremill.tagging._
import sttp.tapir.generic.auto._

class Http() extends Tapir with TapirJsonCirce with TapirSchemas with StrictLogging {
  val failOutput: EndpointOutput[(StatusCode, ErrorOut)] = statusCode and jsonBody[ErrorOut]

  val baseEndpoint: Endpoint[Unit, (StatusCode, ErrorOut), Unit, Any] = endpoint.errorOut(failOutput)

  private val InternalServerError = (StatusCode.InternalServerError, "Internal server error")

  private val failToResponseData: Fail => (StatusCode, String) = {
    case Fail.NotFound(what)      => (StatusCode.NotFound, what)
    case Fail.IncorrectInput(msg) => (StatusCode.BadRequest, msg)
    case _                        => InternalServerError
  }

  def exceptionToErrorOut(e: Exception): (StatusCode, ErrorOut) = {
    logger.error(s"Error $e")
    val (statusCode, message) = e match {
      case f: Fail => failToResponseData(f)
      case _ =>
        logger.error("Exception when processing request", e)
        InternalServerError
    }

    logger.warn(s"Request fail: $message")
    val errorOut = ErrorOut(message)
    (statusCode, errorOut)
  }

  implicit class TaskOut[T](f: Task[T]) {

    /** An extension method for [[Task]], which converts a possibly failed task, to a task which either returns
      * the error converted to an [[ErrorOut]] instance, or returns the successful value unchanged.
      */
    def toOut: Task[Either[(StatusCode, ErrorOut), T]] = {
      f.map(t => t.asRight[(StatusCode, ErrorOut)]).recover { case e: Exception =>
        exceptionToErrorOut(e).asLeft[T]
      }
    }
  }

  override def jsonPrinter: Printer = noNullsPrinter
}

trait TapirSchemas {

  implicit val idPlainCodec: PlainCodec[SecureRandomId] = Codec.string.map(_.asInstanceOf[SecureRandomId])(identity)
  implicit def taggedPlainCodec[U, T](implicit uc: PlainCodec[U]): PlainCodec[U @@ T] =
    uc.map(_.taggedWith[T])(identity)

  implicit val schemaForBigDecimal: Schema[BigDecimal]                       = Schema(SchemaType.SString)
  implicit val schemaForId: Schema[Id]                                       = Schema(SchemaType.SString)
  implicit def schemaForTagged[U, T](implicit uc: Schema[U]): Schema[U @@ T] = uc.asInstanceOf[Schema[U @@ T]]
}

case class ErrorOut(error: String)
