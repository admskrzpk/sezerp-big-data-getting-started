# big-data-getting-started

![CI](https://github.com/sezerp/big-data-getting-started/actions/workflows/ci.yml/badge.svg)

Simple project showing commonly used in Big data technologies such as Spark and Kafka. Main goal is to provide 
simplified project allowing play around kafka, spark and developing api. For simplicity topics such as security has been skipped. 

Technologies used

 - Spark 3.x
 - kafka 2.x
 - Http4s
 - Doobie
 - Monix (version with ZIO in progress)
 - Pureconfig
 - Circe
 - Postgre

NOTES

The designing was made using [cake (anti)pattern](https://kubuszok.com/2018/cake-antipattern/) as is widely used and show some trouble with it. 
Moreover, to don't introduce more concepts such as [Guice](https://github.com/google/guice), [macwire](https://github.com/softwaremill/macwire).
In future should it be solved by ZIO version of this repository which is in progress.

At the beginning Cassandra/ScyllaDB was taken under consideration to store data for ETL. 
However, the Postgres is used as persistence storage for API as well as for data warehouse for ETL. 
The chose was made to keep it simpler.

## Web service

Service provide simple capabilities to manage devices. 

The endpoints don't provide authorization and authentication capabilities for simplicity  

The DB model is maintained using flyway more under [Postgres section](#migration-database) 

## Spark

Module contains spark job showing ETL spark capabilities 

## Kafka

kafka part is build to play and grab some knowledge about kafka capabilities

Running one node kafka cluster

```bash
cd docker
docker-compose -f docker-kafka.yaml up
```

## Postgres

Running postgres database with `journey` database

```bash
cd docker
docker-compose -f docker-postgres.yaml up
```

##Migration database:

```bash
sbt flywayMigrate
```