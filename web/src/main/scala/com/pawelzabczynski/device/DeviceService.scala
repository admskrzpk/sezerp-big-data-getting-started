package com.pawelzabczynski.device

import doobie.Transactor
import monix.eval.Task

class DeviceService(xa: Transactor[Task]) {}

object DeviceService {
  case class Device()
}
