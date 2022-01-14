package com.pawelzabczynski

import com.pawelzabczynski.kafka.MessageProducer
import doobie.Transactor
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

object WebApp {

  def main(args: Array[String]): Unit = {
    val init = new InitModule {}

    val action = (for {
      _xa            <- init.dataBase.txResource
      _kafkaProducer <- init.kafkaProducer.producerResources
    } yield (_xa, _kafkaProducer))
      .use { case (_xa, _kafkaProducer) =>
        val mainModule = new MainModule {
          override def xa: Transactor[Task]           = _xa
          override def kafkaProducer: MessageProducer = _kafkaProducer
        }

        mainModule.httpApi.resources.use(_ => Task.never)
      }

    action.runSyncUnsafe()
  }

}
