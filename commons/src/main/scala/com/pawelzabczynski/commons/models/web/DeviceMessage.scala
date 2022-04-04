package com.pawelzabczynski.commons.models.web

import com.pawelzabczynski.commons.models.Id
import com.softwaremill.tagging.@@

import java.time.Instant

/** @param id device id existing assigned by application NOT device itself
 * @param eventTime time generated on device side, time when even has been generated
 * @param sensor1 optional sensor value
 * @param sensor2 optional sensor value
 * @param sensor3 optional sensor value
 * @param sensor4 optional sensor value
 * @param sensor5 optional sensor value
 */
case class DeviceMessage(
                          id: Id @@ Device,
                          eventTime: Instant,
                          sensor1: Option[Double],
                          sensor2: Option[Double],
                          sensor3: Option[Double],
                          sensor4: Option[Double],
                          sensor5: Option[Double]
                        )
