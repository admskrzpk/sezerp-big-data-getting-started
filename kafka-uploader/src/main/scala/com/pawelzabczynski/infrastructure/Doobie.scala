package com.pawelzabczynski.infrastructure

import com.pawelzabczynski.commons.models.Id
import com.softwaremill.tagging._
import com.typesafe.scalalogging.StrictLogging
import doobie.util.log.{ExecFailure, ProcessingFailure, Success}
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt

import scala.concurrent.duration._
import scala.reflect.runtime.universe.TypeTag

object Doobie
    extends doobie.Aliases
    with doobie.hi.Modules
    with doobie.free.Modules
    with doobie.free.Types
    with doobie.postgres.Instances
    with doobie.util.meta.LegacyInstantMetaInstance
    with doobie.free.Instances
    with doobie.syntax.AllSyntax
    with StrictLogging {

  implicit def idMeta: Meta[Id]                        = implicitly[Meta[String]].asInstanceOf[Meta[Id]]
  implicit def taggedIdMeta[U: TypeTag]: Meta[Id @@ U] = implicitly[Meta[String]].asInstanceOf[Meta[Id @@ U]]

  implicit def taggedStringMeta[U: TypeTag]: Meta[String @@ U] =
    implicitly[Meta[String]].asInstanceOf[Meta[String @@ U]]

  implicit val passwordHashMeta: Meta[PasswordHash[SCrypt]] =
    implicitly[Meta[String]].asInstanceOf[Meta[PasswordHash[SCrypt]]]

  private val SlowThreshold = 200.millis

  implicit val doobieLogHandler: LogHandler = LogHandler {
    case Success(sql, _, exec, processing) =>
      if (exec > SlowThreshold || processing > SlowThreshold) {
        logger.warn(s"Slow query (execution: $exec, processing: $processing): $sql")
      }
      if (!sql.contains("SELECT")) logger.info(s"Processing success (execution: $exec, processing: $processing): $sql")
    case ProcessingFailure(sql, args, exec, processing, failure) =>
      logger.error(s"Processing failure (execution: $exec, processing: $processing): $sql | args: $args", failure)
    case ExecFailure(sql, args, exec, failure) =>
      logger.error(s"Execution failure (execution: $exec): $sql | args: $args", failure)
  }
}
