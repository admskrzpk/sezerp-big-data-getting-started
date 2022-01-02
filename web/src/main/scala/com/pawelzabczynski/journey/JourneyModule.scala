package com.pawelzabczynski.journey

import com.pawelzabczynski.http.Http
import com.pawelzabczynski.utils.BaseModule

trait JourneyModule extends BaseModule {

  lazy val journeyApi = new JourneyApi(http, idGenerator, clock)

  def http: Http

}
