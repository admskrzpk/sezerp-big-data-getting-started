package com.pawelzabczynski.account

import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId}
import com.pawelzabczynski.Fail
import com.pawelzabczynski.account.Account.AccountId
import com.pawelzabczynski.utils.IdGenerator
import doobie.ConnectionIO
import monix.execution.Scheduler.Implicits.global

class AccountService(idGenerator: IdGenerator) {

  def create(name: String): ConnectionIO[Account] = {
    for {
      id <- idGenerator.nextId[Account]().to[ConnectionIO]
      a = Account(id, name)
      acc <- AccountModel.insert(a).map(_ => a)
    } yield acc
  }

  def get(id: AccountId): ConnectionIO[Account] = {
    findOrFail(id)
  }

  def update(id: AccountId, maybeName: Option[String]): ConnectionIO[Account] = {
    for {
      account <- findOrFail(id)
      name = maybeName.fold(account.name)(identity)
      updatedAcc <- AccountModel.update(id, name).map(_ => account.copy(name = name))
    } yield updatedAcc
  }

  private def findOrFail(id: AccountId): ConnectionIO[Account] = {
    for {
      maybeAccount <- AccountModel.find(id)
      account      <- maybeAccount.fold(Fail.NotFound("Account").raiseError[ConnectionIO, Account])(acc => acc.pure[ConnectionIO])
    } yield account
  }

}
