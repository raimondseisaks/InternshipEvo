package reisaks.FinalProject.ServerSide.Kafka
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import java.util.Properties
import org.apache.kafka.common.serialization.StringSerializer

object EventProducer {
  def initProducer: KafkaProducer[String, String] = {
    val producerProperties = new Properties()
    producerProperties.setProperty(
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:6002"
    )
    producerProperties.setProperty(
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName
    )
    producerProperties.setProperty(
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName
    )

    val producer = new KafkaProducer[String, String](producerProperties)
    producer
  }

  def producerSend(producer : KafkaProducer[String, String], topicName: String, key: String, value: String): Unit = {
    val record = new ProducerRecord[String, String](topicName, key, value)
    producer.send(record)
  }
}


