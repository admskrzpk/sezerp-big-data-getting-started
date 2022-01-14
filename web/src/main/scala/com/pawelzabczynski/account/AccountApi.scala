package com.pawelzabczynski.account

import cats.data.NonEmptyList
import com.pawelzabczynski.http.Http
import com.pawelzabczynski.utils.{Id, ServerEndpoints}
import com.softwaremill.tagging.@@
import com.pawelzabczynski.account.AccountApi._
import com.pawelzabczynski.infrastructure.JsonSupport._
import doobie.Transactor
import monix.eval.Task
import sttp.tapir.generic.auto._
import com.pawelzabczynski.infrastructure.Doobie._
import sttp.tapir.EndpointInput

class AccountApi(http: Http, service: AccountService, xa: Transactor[Task]) {

  import http._

  private val Context = "account"

  private val createAccount = baseEndpoint.post
    .in(Context)
    .in(jsonBody[AccountCreateIn])
    .out(jsonBody[AccountCreateOut])
    .serverLogic { case in =>
      (for {
        acc <- service.create(in.name).transact(xa)
      } yield AccountCreateOut(acc)).toOut
    }

  private val getQuery: EndpointInput[Id @@ Account] = query[String]("id").map(_.asInstanceOf[Id @@ Account])(_.asInstanceOf[String])
  private val getAccount = baseEndpoint.get
    .in(Context)
    .in(getQuery)
    .out(jsonBody[AccountGetOut])
    .serverLogic { case id =>
      (for {
        acc <- service.get(id).transact(xa)
      } yield AccountGetOut(acc)).toOut
    }

  private val updateAccount = baseEndpoint.put
    .in(Context)
    .in(jsonBody[AccountUpdateIn])
    .out(jsonBody[AccountUpdateOut])
    .serverLogic { case in =>
      (for {
        acc <- service.update(in.id, in.name).transact(xa)
      } yield AccountUpdateOut(acc)).toOut
    }

  val endpoints: ServerEndpoints = NonEmptyList.of(createAccount, getAccount, updateAccount).map(_.tag("account"))

}

object AccountApi {
  case class AccountCreateIn(name: String)
  case class AccountCreateOut(account: Account)

  case class AccountGetOut(account: Account)

  case class AccountUpdateIn(id: Id @@ Account, name: Option[String])
  case class AccountUpdateOut(account: Account)

}
