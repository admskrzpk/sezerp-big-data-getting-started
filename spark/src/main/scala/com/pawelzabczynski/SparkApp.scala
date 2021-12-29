package com.pawelzabczynski

object SparkApp {

  def main(args: Array[String]): Unit = {
    val mainModule = new MainModule {}
    mainModule.logConfiguration()
  }

}
