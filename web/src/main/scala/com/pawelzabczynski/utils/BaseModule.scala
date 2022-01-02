package com.pawelzabczynski.utils

trait BaseModule {
  def idGenerator: IdGenerator
  def clock: Clock
}
