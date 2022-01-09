package com.pawelzabczynski.device

import cats.data.NonEmptyList
import com.pawelzabczynski.account.Account
import com.pawelzabczynski.http.Http
import com.pawelzabczynski.utils.ServerEndpoints
import com.pawelzabczynski.device.DeviceApi._
import com.pawelzabczynski.infrastructure.JsonSupport._
import sttp.tapir.generic.auto._
import com.pawelzabczynski.utils._
import com.softwaremill.tagging.@@
import doobie.Transactor
import monix.eval.Task
import sttp.tapir.EndpointInput
import com.pawelzabczynski.infrastructure.Doobie._


class DeviceApi(http: Http, service: DeviceService, xa: Transactor[Task]) {

  import http._

  private val ContextPath = "device"

  private val getQuery: EndpointInput[Id @@ Device] = query[String]("id").map(_.asInstanceOf[Id @@ Device])(_.asInstanceOf[String])
  private val getDevice = baseEndpoint.get
    .in(ContextPath)
    .in(getQuery)
    .out(jsonBody[DeviceGetOut])
    .serverLogic { id =>
      (for {
        device <- service.get(id).transact(xa)
      } yield DeviceGetOut(device)).toOut
    }

  private val createDevice = baseEndpoint.post
    .in(ContextPath)
    .in(jsonBody[DeviceCreateIn])
    .out(jsonBody[DeviceCreateOut])
    .serverLogic { case entity =>
      (for {
        device <- service.create(entity).transact(xa)
      } yield DeviceCreateOut(device)).toOut
    }

  val endpoints: ServerEndpoints = NonEmptyList.of(getDevice, createDevice).map(_.tag("device"))

}

object DeviceApi {
  case class DeviceCreateIn(accountId: Id @@ Account, name: String)
  case class DeviceCreateOut(device: Device)

  case class DeviceGetOut(device: Device)

}
