package com.pawelzabczynski.device

import cats.data.NonEmptyList
import com.pawelzabczynski.http.Http
import com.pawelzabczynski.utils.{Clock, IdGenerator, ServerEndpoints}
import com.pawelzabczynski.device.DeviceApi._
import com.pawelzabczynski.infrastructure.JsonSupport._
import com.pawelzabczynski.device.DeviceService.Device
import sttp.tapir.generic.auto._
import com.pawelzabczynski.utils._
import com.softwaremill.tagging.@@
import sttp.tapir.EndpointInput

import java.time.Instant

class DeviceApi(http: Http, idGenerator: IdGenerator, clock: Clock) {

  import http._

  private val ContextPath = "device"

  private val getQuery: EndpointInput[Id @@ Device] = query[String]("id").map(_.asInstanceOf[Id @@ Device])(_.asInstanceOf[String])
  private val getDevice = baseEndpoint.get
  .in(ContextPath)
    .in(getQuery)
    .out(jsonBody[DeviceGetOut])
    .serverLogic { id =>
      (for {
        now <- clock.now()
        _ = println(id)
      } yield DeviceGetOut()).toOut
    }

  private val createDevice = baseEndpoint.post
    .in(ContextPath)
    .in(jsonBody[DeviceCreateIn])
    .out(jsonBody[DeviceCreateOut])
    .serverLogic { case _ =>
      (for {
        id  <- idGenerator.nextId[Device]()
        now <- clock.now()
      } yield DeviceCreateOut(id, now)).toOut
    }

  val endpoints: ServerEndpoints = NonEmptyList.of(getDevice, createDevice).map(_.tag("device"))

}

object DeviceApi {
  case class DeviceCreateIn()
  case class DeviceCreateOut(requestId: Id @@ Device, at: Instant)

  case class DeviceGetOut()

}
