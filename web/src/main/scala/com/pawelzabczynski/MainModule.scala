package com.pawelzabczynski

import com.pawelzabczynski.account.AccountModule
import com.pawelzabczynski.config.ConfigModule
import com.pawelzabczynski.http.{Http, HttpApi}
import com.pawelzabczynski.device.DeviceModule
import com.pawelzabczynski.utils.{BaseModule, Clock, DefaultClock, DefaultIdGenerator, IdGenerator, ServerEndpoints}

trait MainModule extends ConfigModule with BaseModule with AccountModule with DeviceModule {

  override lazy val idGenerator: IdGenerator = DefaultIdGenerator
  override lazy val clock: Clock             = DefaultClock
  override lazy val http: Http               = new Http

  lazy val endpoints: ServerEndpoints = deviceApi.endpoints concatNel accountApi.endpoints
  lazy val httpApi: HttpApi           = new HttpApi(http, endpoints, config.webApp)
}
