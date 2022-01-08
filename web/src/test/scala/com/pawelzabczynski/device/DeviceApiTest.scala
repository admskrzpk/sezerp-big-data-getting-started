package com.pawelzabczynski.device

import com.pawelzabczynski.MainModule
import com.pawelzabczynski.account.AccountApi.{AccountCreateIn, AccountCreateOut}
import com.pawelzabczynski.test.{TestBase, TestEmbeddedPostgres, TestRequests}
import doobie.Transactor
import monix.eval.Task
import org.scalatest.concurrent.Eventually
import com.pawelzabczynski.infrastructure.JsonSupport._

class DeviceApiTest extends TestBase with TestEmbeddedPostgres with Eventually {
  val mainModule = new MainModule {
    override def xa: Transactor[Task] = currentDb.xa
  }
  val requests = new TestRequests(mainModule)

  import requests._


  "/account" when {
    "call post with correct data" should {
      "create new account" in {
        val data = AccountCreateIn("test account")

        val result = accountCreate(data).shouldDeserializeTo[AccountCreateOut]

        result.account.id should fullyMatch regex idPattern
        result.account.name shouldBe data.name
      }
    }

    "call get method" should {
      "" in {

      }
    }

  }
}
