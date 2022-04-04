package com.pawelzabczynski.device

import com.pawelzabczynski.http.Http
import com.pawelzabczynski.kafka.MessageProducer
import com.pawelzabczynski.utils.BaseModule
import doobie.Transactor
import monix.eval.Task

trait DeviceModule extends BaseModule {

  lazy val deviceService = new DeviceService(kafkaProducer, idGenerator, clock)
  lazy val deviceApi     = new DeviceApi(http, deviceService, xa)

  def http: Http
  def xa: Transactor[Task]
  def kafkaProducer: MessageProducer
}
