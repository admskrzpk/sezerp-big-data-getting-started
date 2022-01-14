docker-compose -f ./docker/docker-postgres.yaml up -d
docker-compose -f ./docker/docker-kafka.yaml up -d
sbt "~web/reStart"