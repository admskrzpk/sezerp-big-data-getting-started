package com.pawelzabczynski.test

import com.pawelzabczynski.MainModule
import com.pawelzabczynski.account.Account
import com.pawelzabczynski.device.Device
import com.pawelzabczynski.device.DeviceApi.DeviceCreateIn
import com.pawelzabczynski.infrastructure.JsonSupport._
import com.pawelzabczynski.utils.Id
import com.softwaremill.tagging.@@
import monix.eval.Task
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{Request, Response}

trait TestDeviceRequests { self: TestHttpSupport =>
  val modules: MainModule

  def deviceCreate(entity: DeviceCreateIn): Response[Task] = {
    val request = Request[Task](method = POST, uri = uri"/device")
      .withEntity(entity)

    modules.httpApi.mainRoutes(request).unwrap
  }

  def deviceGet(accountId: Id @@ Account, id: Id @@ Device): Response[Task] = {
    val request = Request[Task](method = GET, uri = buildUri("device", List(UrlParam("deviceId", id), UrlParam("accountId", accountId))))

    modules.httpApi.mainRoutes(request).unwrap
  }
}
