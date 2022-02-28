package com.pawelzabczynski.device

import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId}
import com.pawelzabczynski.Fail
import com.pawelzabczynski.account.{Account, AccountModel}
import com.pawelzabczynski.commons.models.Id
import com.pawelzabczynski.commons.models.web.Device
import com.pawelzabczynski.device.DeviceApi.{DeviceCreateIn, DeviceMessageIn}
import com.pawelzabczynski.kafka.MessageProducer
import com.pawelzabczynski.utils.{Clock, IdGenerator}
import com.softwaremill.tagging.@@
import doobie.ConnectionIO
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

class DeviceService(kafkaProducer: MessageProducer, idGenerator: IdGenerator, clock: Clock) {

  def create(entity: DeviceCreateIn): ConnectionIO[Device] = {
    for {
      maybeAccount <- AccountModel.find(entity.accountId)
      account      <- maybeAccount.fold(Fail.NotFound("Account").raiseError[ConnectionIO, Account])(_.pure[ConnectionIO])
      device       <- entityToDevice(entity, account.id).to[ConnectionIO]
      _            <- DeviceModel.insert(device)
    } yield device
  }

  def get(accountId: Id @@ Account, id: Id @@ Device): ConnectionIO[Device] = {
    for {
      maybeDevice <- DeviceModel.findBy(accountId, id)
      device      <- maybeDevice.fold(Fail.NotFound("Device").raiseError[ConnectionIO, Device])(_.pure[ConnectionIO])
    } yield device
  }

  def sendMessage(entity: DeviceMessageIn): ConnectionIO[Unit] = {
    for {
      maybeDevice <- DeviceModel.findBy(entity.accountId, entity.message.id)
      _ <- maybeDevice.fold(Fail.NotFound("Device").raiseError[ConnectionIO, Device])(_.pure[ConnectionIO])
      _ <- kafkaProducer.send(entity.message.id, entity.message).void.to[ConnectionIO]
    } yield ()
  }

  private def entityToDevice(entity: DeviceCreateIn, accountId: Id @@ Account): Task[Device] = {
    for {
      id <- idGenerator.nextId[Device]()
      device = Device(id, accountId, entity.name)
    } yield device
  }
}
