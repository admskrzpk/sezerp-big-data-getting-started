package com.dendritcloud.config

case class Sensitive(value: String) extends AnyVal {
  override def toString: String = value
}
