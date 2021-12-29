package com.pawelzabczynski

import cats.data.NonEmptyList
import com.softwaremill.tagging.@@
import monix.eval.Task
import sttp.tapir.server.ServerEndpoint
import tsec.common.SecureRandomId


package object utils {
  type Id  = SecureRandomId

  implicit class RichString(val s: String) extends AnyVal {
    def asId[T]: Id @@ T                 = s.asInstanceOf[Id @@ T]
  }

  type ServerEndpoints = NonEmptyList[ServerEndpoint[_, _, _, Any, Task]]

}
