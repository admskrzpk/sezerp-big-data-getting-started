package com.pawelzabczynski.commons.models.web

import com.pawelzabczynski.models.Id
import com.softwaremill.tagging.@@

case class Device(id: Id @@ Device, accountId: String, name: String)
