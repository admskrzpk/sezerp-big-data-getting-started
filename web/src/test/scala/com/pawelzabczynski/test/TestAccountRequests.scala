package com.pawelzabczynski.test

import com.pawelzabczynski.MainModule
import com.pawelzabczynski.account.Account.AccountId
import com.pawelzabczynski.account.AccountApi.{AccountCreateIn, AccountUpdateIn}
import monix.eval.Task
import org.http4s.syntax.literals._
import org.http4s.{Request, Response}
import com.pawelzabczynski.infrastructure.JsonSupport._

import java.security.URIParameter

trait TestAccountRequests { self: TestHttpSupport =>

  val modules: MainModule

  def accountCreate(entity: AccountCreateIn): Response[Task] = {
    val request = Request[Task](method = POST, uri = uri"/account")
      .withEntity(entity)

    modules.httpApi.mainRoutes(request).unwrap
  }

  def accountGet(id: AccountId): Response[Task] = {
    val request = Request[Task](method = GET, uri = buildUri("account", List(UrlParam("id", id))))

    modules.httpApi.mainRoutes(request).unwrap
  }

  def accountUpdate(entity: AccountUpdateIn): Response[Task] = {
    val request = Request[Task](method = PUT, uri = uri"/account")
      .withEntity(entity)

    modules.httpApi.mainRoutes(request).unwrap
  }

}
