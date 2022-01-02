package com.pawelzabczynski

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

object WebApp {

  def main(args: Array[String]): Unit = {
    val mainModule = new MainModule {}
    mainModule.logConfiguration()

    mainModule.httpApi.resources.use(_ => Task.never).runSyncUnsafe()
  }

}
