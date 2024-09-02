package reisaks.FinalProject.ServerSide.Kafka

import cats.effect.{IO, IOApp}
import io.github.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}

object EmbeddedKafka extends IOApp.Simple with EmbeddedKafka {
  implicit val config: EmbeddedKafkaConfig = EmbeddedKafkaConfig(kafkaPort = 9092, zooKeeperPort = 9091)
   def run(): IO[Unit] =  IO(withRunningKafka {
      EventConsumer.startConsumer() //Start consumer which listens topic
    })
}




