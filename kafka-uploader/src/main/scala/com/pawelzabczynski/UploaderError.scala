package com.pawelzabczynski

abstract class UploaderError extends Throwable {
  def message: String
}

object UploaderError {}
