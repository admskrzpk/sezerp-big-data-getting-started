package com.pawelzabczynski.account

import com.pawelzabczynski.account.Account.AccountId
import com.pawelzabczynski.utils.Id
import com.softwaremill.tagging.@@

object AccountModel {

}


case class Account(id: AccountId, name: String)
object Account {
  type AccountId = Id @@ Account
}
