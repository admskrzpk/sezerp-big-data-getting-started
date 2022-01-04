package com.pawelzabczynski.device

import com.pawelzabczynski.http.Http
import com.pawelzabczynski.utils.BaseModule

trait DeviceModule extends BaseModule {

  lazy val journeyApi = new DeviceApi(http, idGenerator, clock)

  def http: Http

}
