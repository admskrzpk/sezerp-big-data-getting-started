package com.pawelzabczynski.test

import com.pawelzabczynski.kafka.MessageProducer
import org.scalatest.{BeforeAndAfterAll, Suite}

trait TestKafka extends BeforeAndAfterAll { self: Suite =>

  var currentKafkaProducer: MessageProducer = _

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    currentKafkaProducer = MessageProducer.createUnsafe(TestConfig.kafka)
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    currentKafkaProducer.close()
  }
}
