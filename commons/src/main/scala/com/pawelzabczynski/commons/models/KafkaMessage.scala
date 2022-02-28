package com.pawelzabczynski.commons.models

import com.softwaremill.tagging.@@

import java.time.Instant

object KafkaMessages {

  trait KafkaMessage[T] {
    def id: Id @@ T
    def eventTime: Instant
  }
}
