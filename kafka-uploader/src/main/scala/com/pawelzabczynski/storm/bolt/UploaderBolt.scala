package com.pawelzabczynski.storm.bolt

import com.pawelzabczynski.commons.models.web.DeviceMessage
import com.pawelzabczynski.storm.{DbConfig, JdbcUploaderBoltConfig, Tuples}
import org.apache.storm.task.{OutputCollector, TopologyContext}
import org.apache.storm.topology.{IRichBolt, OutputFieldsDeclarer}
import org.apache.storm.tuple.Tuple
import com.pawelzabczynski.storm.bolt.JdbcUploader.{InsertBatchError, JdbcError}
import com.typesafe.scalalogging.LazyLogging
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import java.sql.{PreparedStatement, Time}
import scala.collection.mutable
import java.util.{Properties, Map => JMap}
import java.lang.{Double => JDouble}

class UploaderBolt(config: JdbcUploaderBoltConfig) extends IRichBolt with LazyLogging {
  var collector: OutputCollector                          = _
  var boltConfig: JMap[String, AnyRef]                    = _
  var records: mutable.ListBuffer[(Tuple, DeviceMessage)] = _
  var jdbcUploader: JdbcUploader                          = _

  override def prepare(_conf: JMap[String, AnyRef], _context: TopologyContext, _collector: OutputCollector): Unit = {
    collector = _collector
    boltConfig = _conf
    records = mutable.ListBuffer.empty
    jdbcUploader = JdbcUploader.create(config.db)
  }

  override def execute(input: Tuple): Unit = {
    try {
      if (input.contains(Tuples.Message)) {
        input.getValueByField(Tuples.Message) match {
          case msg: DeviceMessage =>
            if (records.size + 1 < config.batchSize) {
              records.addOne((input, msg))
            } else {
              records.addOne((input, msg))
              jdbcUploader.insertBatch(records.map(_._2).toList) match {
                case Right(_) => records.foreach { case (t, _) => collector.ack(t) }
                case Left(e) =>
                  logger.error(s"Error occurred when try insert batch, failing all batch", e)
                  records.foreach { case (t, _) => collector.fail(t) }
                  records.clear()
              }
            }
          case msg =>
            logger.error(s"Incorrect message type, skipping $msg")
            collector.ack(input)
        }
      } else {
        logger.error(s"Tuple $input does not contains ${Tuples.Message} field, skipping incorrect message")
        collector.ack(input)
      }
    } catch {
      case t: Throwable =>
        logger.error("Error occurred when try insert data", t)
        collector.fail(input)
    }
  }

  override def declareOutputFields(declarer: OutputFieldsDeclarer): Unit = {
    // Do not declare any field due to the bolt is saving data into DB
  }

  override def getComponentConfiguration: JMap[String, AnyRef] = {
    boltConfig
  }

  override def cleanup(): Unit = {}
}

class JdbcUploader(dataSource: HikariDataSource) {

  private val query = s"INSERT INTO device_events(id, event_time, sensor_1, sensor_2, sensor_3, sensor_4, sensor_5) VALUES(?,?,?,?,?,?,?)"

  def insertBatch(xs: List[DeviceMessage]): Either[JdbcError, Unit] = {
    val statement = dataSource.getConnection.prepareStatement(query)
    try {
      val batch = createBatch(xs, statement)

      batch.executeBatch()
      batch.close()
      Right(())
    } catch {
      case t: Throwable => Left(InsertBatchError(t))
    }
  }

  private def createBatch(xs: List[DeviceMessage], preparedStatement: PreparedStatement): PreparedStatement = {
    def loop(ys: List[DeviceMessage], acc: PreparedStatement): PreparedStatement = {
      ys match {
        case head :: tail =>
          acc.setString(0, head.id)
          acc.setTime(1, new Time(head.eventTime.toEpochMilli))
          acc.setDouble(2, head.sensor1.fold(JDouble.MIN_VALUE)(v => JDouble.valueOf(v)))
          acc.setDouble(3, head.sensor2.fold(JDouble.MIN_VALUE)(v => JDouble.valueOf(v)))
          acc.setDouble(4, head.sensor3.fold(JDouble.MIN_VALUE)(v => JDouble.valueOf(v)))
          acc.setDouble(5, head.sensor4.fold(JDouble.MIN_VALUE)(v => JDouble.valueOf(v)))
          acc.setDouble(6, head.sensor5.fold(JDouble.MIN_VALUE)(v => JDouble.valueOf(v)))
          acc.addBatch()
          loop(tail, acc)
        case Nil => acc
      }
    }

    loop(xs, preparedStatement)
  }
}

object JdbcUploader {
  private val DataSourceConfig = "dataSource"
  private val UrlConfig        = s"$DataSourceConfig.url"
  private val UserConfig       = s"$DataSourceConfig.user"
  private val PasswordConfig   = s"$DataSourceConfig.password"
  private val DriverConfig     = "dataSourceClassName"

  sealed trait JdbcError
  case class InsertBatchError(err: Throwable) extends JdbcError

  def create(config: DbConfig): JdbcUploader = {
    val hikariProperties = new Properties()
    hikariProperties.put(UrlConfig, config.url)
    hikariProperties.put(UserConfig, config.username)
    hikariProperties.put(PasswordConfig, config.password)
    hikariProperties.put(DriverConfig, config.driver)
    val hikariConfig = new HikariConfig(hikariProperties)
    val dataSource   = new HikariDataSource(hikariConfig)

    new JdbcUploader(dataSource)
  }
}

object UploaderBolt {
  val BoltId = "measurement-uploader-bolt"

  def create(config: JdbcUploaderBoltConfig): IRichBolt = {
    new UploaderBolt(config)
  }
}
