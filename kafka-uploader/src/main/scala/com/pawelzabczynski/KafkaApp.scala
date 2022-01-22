package com.pawelzabczynski

import com.pawelzabczynski.kafka.MainModule

object KafkaApp {

  def main(args: Array[String]): Unit = {
    val mainModule = new MainModule {}
    mainModule.logConfiguration()
  }

}
