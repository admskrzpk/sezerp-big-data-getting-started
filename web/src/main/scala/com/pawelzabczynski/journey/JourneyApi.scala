package com.pawelzabczynski.journey

import cats.data.NonEmptyList
import com.pawelzabczynski.http.Http
import com.pawelzabczynski.utils.{Clock, IdGenerator, ServerEndpoints}
import com.pawelzabczynski.journey.JourneyApi._
import com.pawelzabczynski.infrastructure.JsonSupport._
import com.pawelzabczynski.journey.JourneyService.Journey
import sttp.tapir.generic.auto._
import com.pawelzabczynski.utils._
import com.softwaremill.tagging.@@

import java.time.Instant

class JourneyApi(http: Http, idGenerator: IdGenerator, clock: Clock) {

  import http._

  private val ContextPath = "journey"

  private val bookJourney = baseEndpoint.post
    .in(ContextPath)
    .in(jsonBody[JourneyBookIn])
    .out(jsonBody[JourneyBookOut])
    .serverLogic { case _ =>
      (for {
        id  <- idGenerator.nextId[Journey]()
        now <- clock.now()
      } yield JourneyBookOut(id, now)).toOut
    }

  val endpoints: ServerEndpoints = NonEmptyList.of(bookJourney).map(_.tag("journey"))

}

object JourneyApi {
  case class JourneyBookIn()
  case class JourneyBookOut(requestId: Id @@ Journey, at: Instant)

}
