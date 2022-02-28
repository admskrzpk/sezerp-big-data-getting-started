package com.pawelzabczynski.infrastructure

import io.circe.generic.AutoDerivation
import com.pawelzabczynski.commons.json.{JsonSupport => CommonJsonSupport}

object JsonSupport extends AutoDerivation with CommonJsonSupport {}
