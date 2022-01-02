package com.pawelzabczynski.http

import com.pawelzabczynski.utils.ServerEndpoints
import monix.eval.Task
import org.http4s.HttpRoutes

class ApiDocsSupport(http: Http, contextPath: String) {
  def apply(es: ServerEndpoints): HttpRoutes[Task] = {
    ???
  }
}
