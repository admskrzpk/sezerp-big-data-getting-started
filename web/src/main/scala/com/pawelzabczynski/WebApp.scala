package com.pawelzabczynski

import doobie.Transactor
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

object WebApp {

  def main(args: Array[String]): Unit = {
    val init = new InitModule {}

    val action = init.dataBase.txResource.use { _xa =>
      val mainModule = new MainModule {
        override def xa: Transactor[Task] = _xa
      }

      mainModule.httpApi.resources.use(_ => Task.never)
    }

    action.runSyncUnsafe()
  }

}
