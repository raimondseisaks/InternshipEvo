package reisaks.FinalProject.ServerSide.Kafka

import cats.effect.{IO, IOApp}
import io.github.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}

object EmbeddedKafka extends IOApp.Simple with EmbeddedKafka {
  implicit val config: EmbeddedKafkaConfig = EmbeddedKafkaConfig(kafkaPort = 6002, zooKeeperPort = 6001)
   def run(): IO[Unit] =  IO(withRunningKafka {
      EventConsumer.startConsumer() //Start consumer which listens topic
    })
}




