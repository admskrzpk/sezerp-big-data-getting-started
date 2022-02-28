package com.pawelzabczynski

object KafkaApp {

  def main(args: Array[String]): Unit = {
    val mainModule = new MainModule {}
    mainModule.logConfiguration()
  }

}
