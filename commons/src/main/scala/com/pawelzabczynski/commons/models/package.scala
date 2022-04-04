package com.pawelzabczynski.commons

import com.softwaremill.tagging.@@
import tsec.common.SecureRandomId

package object models {
  type Id = SecureRandomId

  implicit class RichString(val s: String) extends AnyVal {
    def asId[T]: Id @@ T = s.asInstanceOf[Id @@ T]
  }

  case class Sensitive(value: String) extends AnyVal {
    override def toString: String = "***"
  }
}
