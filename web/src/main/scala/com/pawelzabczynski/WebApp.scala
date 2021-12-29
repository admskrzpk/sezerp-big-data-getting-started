package com.pawelzabczynski

object WebApp {

  def main(args: Array[String]): Unit = {
    val mainModule = new MainModule {}
    mainModule.logConfiguration()
  }

}
