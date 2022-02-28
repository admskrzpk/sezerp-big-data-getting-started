package com.pawelzabczynski

import cats.data.NonEmptyList
import monix.eval.Task
import sttp.tapir.server.ServerEndpoint

package object utils {
  type ServerEndpoints = NonEmptyList[ServerEndpoint[_, _, _, Any, Task]]
}
