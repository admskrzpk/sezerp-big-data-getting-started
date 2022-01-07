package com.pawelzabczynski.account

import com.pawelzabczynski.account.Account.AccountId
import com.pawelzabczynski.utils.Id
import com.softwaremill.tagging.@@
import doobie.ConnectionIO
import com.pawelzabczynski.infrastructure.Doobie._
import cats.syntax.functor._

object AccountModel {
  def insert(a: Account): ConnectionIO[Unit] = {
    sql"""INSERT INTO accounts (id, name) VALUES (${a.id}, ${a.name})""".stripMargin.update.run.void
  }

  def find(id: AccountId): ConnectionIO[Option[Account]] = {
    sql"""SELECT id, name FROM accounts 
         WHERE id = $id""".stripMargin.query[Account].option
  }

  def delete(id: AccountId): ConnectionIO[Unit] = {
    sql"""DELETE FROM accounts WHERE id = $id""".update.run.void
  }

  def update(id: AccountId, name: String): ConnectionIO[Unit] = {
    sql"""UPDATE accounts
            SET name = $name
          WHERE id = $id
       """.update.run.void
  }

}

case class Account(id: Id @@ Account, name: String)
object Account {
  type AccountId = Id @@ Account
}
