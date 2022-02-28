package com.pawelzabczynski

abstract class Fail extends Exception

object Fail {
  case class NotFound(message: String)       extends Fail
  case class IncorrectInput(message: String) extends Fail
}
