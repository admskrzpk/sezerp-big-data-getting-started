package com.pawelzabczynski

abstract class Fail extends Exception

object Fail {
  case class NotFound(what: String)
  case class IncorrectInput(msg: String) extends Fail
}
