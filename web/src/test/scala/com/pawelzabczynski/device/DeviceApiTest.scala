package com.pawelzabczynski.device

import com.pawelzabczynski.MainModule
import com.pawelzabczynski.device.DeviceApi.{DeviceCreateIn, DeviceCreateOut, DeviceGetOut}
import com.pawelzabczynski.test.{TestBase, TestEmbeddedPostgres, TestKafka, TestRequests}
import doobie.Transactor
import monix.eval.Task
import org.scalatest.concurrent.Eventually
import com.pawelzabczynski.infrastructure.JsonSupport._
import com.pawelzabczynski.kafka.MessageProducer


class DeviceApiTest extends TestBase with TestEmbeddedPostgres with TestKafka with Eventually {
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
}
