package com.pawelzabczynski.device

import cats.implicits.catsSyntaxOptionId
import com.pawelzabczynski.MainModule
import com.pawelzabczynski.device.DeviceApi.{DeviceCreateIn, DeviceCreateOut, DeviceGetOut, DeviceMessageIn, DeviceMessageOut}
import com.pawelzabczynski.test.{TestBase, TestConfig, TestEmbeddedPostgres, TestKafka, TestRequests}
import doobie.Transactor
import monix.eval.Task
import org.scalatest.concurrent.Eventually
import com.pawelzabczynski.infrastructure.JsonSupport._
import com.pawelzabczynski.kafka.KafkaMessages.{DeviceMessage, KafkaMessage}
import com.pawelzabczynski.kafka.MessageProducer
import io.github.embeddedkafka.EmbeddedKafka
import com.pawelzabczynski.kafka.KafkaSerializationSupport._
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.duration.DurationInt


class DeviceApiTest extends TestBase with TestEmbeddedPostgres with TestKafka with EmbeddedKafka with Eventually {
  val mainModule = new MainModule {
    override def xa: Transactor[Task] = currentDb.xa

    override def kafkaProducer: MessageProducer = currentKafkaProducer
  }
  val requests = new TestRequests(mainModule)

  import requests._

  "/device" when {
    "call post with correct data" should {
      "create new device related to account" in {
        val account = createAccount()
        val device = DeviceCreateIn(account.id, "device 1")
        val response = deviceCreate(device).shouldDeserializeTo[DeviceCreateOut]

        response.device.id should fullyMatch regex idPattern
        response.device.accountId shouldBe account.id
        response.device.name shouldBe "device 1"
      }
    }

    "call get method" should {
      "return existing device for given id" in {
        val account = createAccount()
        val device = DeviceCreateIn(account.id, "device 1")
        val deviceResponse = deviceCreate(device).shouldDeserializeTo[DeviceCreateOut]
        val response = deviceGet(account.id, deviceResponse.device.id).shouldDeserializeTo[DeviceGetOut]

        response.device.id shouldBe deviceResponse.device.id
        response.device.name shouldBe "device 1"
      }
    }
  }

  "/device/message" when {
    "insert valid message" should {
      "push message into kafka" in {
          withRunningKafka {
            val account = createAccount()
            val device = DeviceCreateIn(account.id, "device 1")
            val deviceResponse = deviceCreate(device).shouldDeserializeTo[DeviceCreateOut]
            val now = testClock.now().runSyncUnsafe(1.second)
            val messageRequest = DeviceMessageIn(account.id, message = DeviceMessage(deviceResponse.device.id, now, 10.1.some, None, None, None, None))
            devicePushMessage(messageRequest).shouldDeserializeTo[DeviceMessageOut]

            val resultMessage = EmbeddedKafka.consumeFirstMessageFrom[KafkaMessage[Device]](TestConfig.kafka.topic)

            resultMessage.id shouldBe deviceResponse.device.id
          }
      }
    }
  }

}
