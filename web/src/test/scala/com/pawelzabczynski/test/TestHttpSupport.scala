package com.pawelzabczynski.test

import cats.data.OptionT
import cats.effect.Sync
import com.pawelzabczynski.MainModule
import com.pawelzabczynski.http.ErrorOut
import io.circe.{Decoder, Encoder, Json}
import org.http4s.{EntityDecoder, EntityEncoder, Response, Status, Uri}
import com.pawelzabczynski.infrastructure.JsonSupport._
import com.typesafe.scalalogging.Logger
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.http4s.dsl.Http4sDsl
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration.DurationInt
import scala.reflect.ClassTag

trait TestHttpSupport extends Http4sDsl[Task] with Matchers {
  private val logger: Logger = Logger(classOf[TestHttpSupport])

  val modules: MainModule

  // http4s has literals. However, it is not working and raising errors `exception during macro expansion`
  def buildUri(base: String, urlParams: List[UrlParam]): Uri = {
    if (urlParams.isEmpty) stringToUri(base)
    else {
      val urlStr = urlParams
        .foldLeft(s"$base?")((acc, params) => s"$acc&${params.name}=${params.value}")

      stringToUri(urlStr)
    }
  }

  def stringToUri(endpoint: String): Uri = {
    Uri.fromString(endpoint) match {
      case Left(_) =>
        throw new IllegalArgumentException(s"Given uri: $endpoint is invalid.")
      case Right(uri) => uri
    }
  }

  implicit def entityEncoderFromCirce[F[_]: Sync, T: Encoder]: EntityEncoder[F, T] = {
    org.http4s.circe.jsonEncoderWithPrinterOf[F, T](noNullsPrinter)
  }

  implicit def entityDecoderFromCirce[F[_]: Sync, T: Decoder]: EntityDecoder[F, T] = {
    org.http4s.circe.jsonOf[F, T]
  }

  implicit class RichTask[T](t: Task[T]) {
    def unwrap: T = {
      t.runSyncUnsafe(1.minute)
    }
  }

  implicit class RichOptionTResponse(t: OptionT[Task, Response[Task]]) {
    def unwrap: Response[Task] = t.value.unwrap match {
      case None    => fail("No response!")
      case Some(r) => r
    }
  }

  implicit class RichResponse(r: Response[Task]) {
    def shouldDeserializeTo[T: Decoder: ClassTag]: T = {
      if (r.status != Status.Ok) {
        fail(s"Response status: ${r.status}: ${r.attemptAs[String].value.unwrap}")
      } else {
        val attemptResult = r.attemptAs[T].value.unwrap
        attemptResult match {
          case Left(df) => fail(s"Cannot deserialize to ${implicitly[ClassTag[T]].runtimeClass.getName}:\n$df")
          case Right(v) => v
        }
      }
    }

    def shouldDeserializeToError: String = {
      val attemptResult = r.attemptAs[ErrorOut].value.unwrap
      attemptResult match {
        case Left(df) => fail(s"Cannot deserialize to error:\n$df")
        case Right(v) => v.error
      }
    }
  }

  implicit class RichStringData(data: String) {
    def toJsonObject: Json =
      io.circe.parser
        .parse(data)
        .left
        .map { e =>
          logger.error("Error", e)
          e
        }
        .toOption
        .get
  }

}

case class UrlParam(name: String, value: String)
