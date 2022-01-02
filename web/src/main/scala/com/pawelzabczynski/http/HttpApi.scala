package com.pawelzabczynski.http

import cats.data.{Kleisli, OptionT}
import cats.effect.Resource
import cats.implicits.toSemigroupKOps
import com.pawelzabczynski.Fail
import com.pawelzabczynski.utils.ServerEndpoints
import monix.eval.Task
import monix.execution.Scheduler
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.{HttpApp, HttpRoutes, Response}
import org.http4s.server.{Router, Server}
import sttp.tapir.DecodeResult
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.openapi.{Server => TapirServer}
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import sttp.tapir.server.http4s._
import org.http4s.syntax.kleisli._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.server.{DecodeFailureContext, DecodeFailureHandler, DecodeFailureHandling, DefaultDecodeFailureResponse, ServerDefaults}
import monix.execution.Scheduler.Implicits.global

class HttpApi(http: Http, endpoints: ServerEndpoints, config: HttpConfig) {

  private val ContextPath                       = "/api/v1"
  private lazy val docsRoutes: HttpRoutes[Task] = toDocsRoutes(endpoints)
  private lazy val mainRoutes                   = toRoutes(endpoints)

  lazy val resources: Resource[Task, Server[Task]] = {
    val app: HttpApp[Task] = Router(
      s"$ContextPath" -> (mainRoutes <+> docsRoutes <+> respondWithNotFound)
    ).orNotFound

    BlazeServerBuilder[Task](Scheduler.global)
      .bindHttp(config.port, config.host)
      .withHttpApp(app)
      .resource
  }

  private val respondWithNotFound: HttpRoutes[Task] = Kleisli(_ => OptionT.pure(Response.notFound))

  def toRoutes(es: ServerEndpoints): HttpRoutes[Task] = {
    implicit val serverOptions: Http4sServerOptions[Task] = Http4sServerOptions
      .default[Task]
      .copy(
        decodeFailureHandler = decodeFailureHandler
      )

    Http4sServerInterpreter.toRoutes(es.toList)
  }

  private val decodeFailureHandler: DecodeFailureHandler = {
    def failResponse(defaultDecodeFailureResponse: DefaultDecodeFailureResponse, msg: String): DecodeFailureHandling =
      DecodeFailureHandling.response(http.failOutput)((defaultDecodeFailureResponse.status, ErrorOut(msg)))

    val defaultHandler = ServerDefaults.decodeFailureHandler.copy(response = failResponse)

    {
      // if an exception is thrown when decoding an input, and the exception is a Fail, responding basing on the Fail
      case DecodeFailureContext(_, DecodeResult.Error(_, f: Fail), _) =>
        DecodeFailureHandling.response(http.failOutput)(http.exceptionToErrorOut(f))
      // otherwise, converting the decode input failure into a response using tapir's defaults
      case ctx =>
        defaultHandler(ctx)
    }
  }

  private def toDocsRoutes(es: ServerEndpoints): HttpRoutes[Task] = {
    val openapi = OpenAPIDocsInterpreter
      .serverEndpointsToOpenAPI(es.toList, "JourneyApp", "1.0")
      .servers(List(TapirServer(s"$ContextPath", None)))
    val yaml = openapi.toYaml
    new SwaggerHttp4s(yaml).routes[Task]
  }
}
