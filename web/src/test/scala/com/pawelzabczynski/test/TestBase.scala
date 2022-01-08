package com.pawelzabczynski.test

import org.scalatest.OptionValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

trait TestBase extends AnyWordSpec with Matchers with OptionValues {
  val testClock = new TestClock()
  val idPattern = "[a-zA-Z\\d]{64}"
}
