package com.pawelzabczynski.device

import cats.data.NonEmptyList
import com.pawelzabczynski.account.Account
import com.pawelzabczynski.http.Http
import com.pawelzabczynski.utils.ServerEndpoints
import com.pawelzabczynski.device.DeviceApi._
import com.pawelzabczynski.infrastructure.JsonSupport._
import sttp.tapir.generic.auto._
import com.pawelzabczynski.commons.models.Id
import com.pawelzabczynski.commons.models.web.Device
import com.softwaremill.tagging.@@
import doobie.Transactor
import monix.eval.Task
import sttp.tapir.EndpointInput
import com.pawelzabczynski.infrastructure.Doobie._
import com.pawelzabczynski.commons.models.web.DeviceMessage

class DeviceApi(http: Http, service: DeviceService, xa: Transactor[Task]) {

  import http._

  private val ContextPath = "device"

  private val accountIdQuery: EndpointInput[Id @@ Account] =
    query[String]("accountId").map(_.asInstanceOf[Id @@ Account])(_.asInstanceOf[String])
  private val deviceIdQuery: EndpointInput[Id @@ Device] =
    query[String]("deviceId").map(_.asInstanceOf[Id @@ Device])(_.asInstanceOf[String])
  private val getDeviceQuery: EndpointInput[(Id @@ Device, Id @@ Account)] = deviceIdQuery.and(accountIdQuery)
  private val getDevice = baseEndpoint.get
    .in(ContextPath)
    .in(getDeviceQuery)
    .out(jsonBody[DeviceGetOut])
    .serverLogic { case (deviceId, accountId) =>
      (for {
        device <- service.get(accountId, deviceId).transact(xa)
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

  private val sendMessage = baseEndpoint.post
    .in(ContextPath / "message")
    .in(jsonBody[DeviceMessageIn])
    .out(jsonBody[DeviceMessageOut])
    .serverLogic { case in =>
      (for {
        _ <- service.sendMessage(in).transact(xa)
      } yield DeviceMessageOut()).toOut
    }

  val endpoints: ServerEndpoints = NonEmptyList.of(getDevice, createDevice, sendMessage).map(_.tag("device"))

}

object DeviceApi {
  case class DeviceCreateIn(accountId: Id @@ Account, name: String)
  case class DeviceCreateOut(device: Device)

  case class DeviceGetOut(device: Device)

  case class DeviceMessageIn(accountId: Id @@ Account, message: DeviceMessage)
  case class DeviceMessageOut()

}
