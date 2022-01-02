package com.pawelzabczynski

import com.pawelzabczynski.config.ConfigModule
import com.pawelzabczynski.http.{Http, HttpApi}
import com.pawelzabczynski.journey.JourneyModule
import com.pawelzabczynski.utils.{BaseModule, Clock, DefaultClock, DefaultIdGenerator, IdGenerator, ServerEndpoints}

trait MainModule extends ConfigModule with BaseModule with JourneyModule {

  override lazy val idGenerator: IdGenerator = DefaultIdGenerator
  override lazy val clock: Clock             = DefaultClock

  lazy val http: Http                 = new Http
  lazy val endpoints: ServerEndpoints = journeyApi.endpoints
  lazy val httpApi: HttpApi           = new HttpApi(http, endpoints, config.webApp)
}
