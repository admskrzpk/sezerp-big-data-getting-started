package com.pawelzabczynski.test

import com.pawelzabczynski.MainModule
import com.pawelzabczynski.account.Account
import com.pawelzabczynski.account.AccountApi.{AccountCreateIn, AccountCreateOut}
import monix.eval.Task
import org.http4s.Request
import org.http4s.implicits.http4sLiteralsSyntax
import com.pawelzabczynski.infrastructure.JsonSupport._

import java.util.UUID

class TestRequests(override val modules: MainModule) extends TestAccountRequests with TestDeviceRequests with TestHttpSupport {

  def createAccount(): Account = {
    val name    = UUID.randomUUID().toString
    val entity  = AccountCreateIn(name)
    val request = Request[Task](method = POST, uri = uri"/account").withEntity(entity)

    modules.httpApi.mainRoutes(request).unwrap.shouldDeserializeTo[AccountCreateOut].account
  }

}
