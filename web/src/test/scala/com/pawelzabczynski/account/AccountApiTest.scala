package com.pawelzabczynski.account

import com.pawelzabczynski.MainModule
import com.pawelzabczynski.account.AccountApi.{AccountCreateIn, AccountCreateOut, AccountGetOut, AccountUpdateIn, AccountUpdateOut}
import com.pawelzabczynski.test.{TestBase, TestEmbeddedPostgres, TestRequests}
import doobie.Transactor
import monix.eval.Task
import org.scalatest.concurrent.Eventually
import com.pawelzabczynski.infrastructure.JsonSupport._

class AccountApiTest extends TestBase with TestEmbeddedPostgres with Eventually {

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
      "return existing account" in {
        val account = createAccount()
        val response  = accountGet(account.id).shouldDeserializeTo[AccountGetOut]

        response.account.id should fullyMatch regex idPattern
        response.account.name shouldBe account.name
      }

      "call put method" should {
        "update account name" in {
          val account = createAccount()
          val entity = AccountUpdateIn(account.id, Some("New name"))
          val updatedAcc = accountUpdate(entity).shouldDeserializeTo[AccountUpdateOut]

          updatedAcc.account.id shouldBe account.id
          updatedAcc.account.name shouldBe entity.name.value
        }

        "not affect account name as new name is not provided" in {
          val account = createAccount()
          val entity = AccountUpdateIn(account.id, None)
          val updatedAcc = accountUpdate(entity).shouldDeserializeTo[AccountUpdateOut]

          updatedAcc.account.id shouldBe account.id
          updatedAcc.account.name shouldBe account.name
        }
      }
    }

  }

}
