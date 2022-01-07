package com.pawelzabczynski.device

import com.pawelzabczynski.utils.Id
import com.softwaremill.tagging.@@
import doobie.ConnectionIO
import com.pawelzabczynski.infrastructure.Doobie._
import cats.syntax.functor._
import com.pawelzabczynski.account.Account.AccountId

object DeviceModel {

  def insert(d: Device): ConnectionIO[Unit] = {
    sql"""INSERT INTO devices (id, account_id, name) VALUES (${d.id}, ${d.accountId}, ${d.name})""".stripMargin.update.run.void
  }

  def delete(id: Id, accountId: AccountId): ConnectionIO[Unit] = {
    sql"""DELETE FROM devices WHERE account_id = $accountId AND id = $id""".update.run.void
  }

}

case class Device(id: Id @@ Device, accountId: String, name: String)
object Device {
  type DeviceId =
    Id @@ Device // cannot be used whenever tapir is used due to problem during macros expansion which cannot find appropriate decoder/encoder automatically when type alias used
}
