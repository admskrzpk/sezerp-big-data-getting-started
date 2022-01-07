package com.pawelzabczynski.account

import com.pawelzabczynski.http.Http
import com.pawelzabczynski.utils.BaseModule
import doobie.Transactor
import monix.eval.Task

trait AccountModule extends BaseModule {

  lazy val accountService = new AccountService(idGenerator)
  lazy val accountApi     = new AccountApi(http, accountService, xa)

  def xa: Transactor[Task]
  def http: Http
}
