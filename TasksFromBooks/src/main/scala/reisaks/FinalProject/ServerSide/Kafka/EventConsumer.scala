package reisaks.FinalProject.ServerSide.Kafka
import scala.jdk.CollectionConverters._
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import java.util.Properties
import org.apache.kafka.common.serialization.StringDeserializer
import java.io.{FileWriter, PrintWriter}



object EventConsumer {
  def initConsumer: KafkaConsumer[String, String] = {
    val consumerProperties = new Properties()
    consumerProperties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:6002")
    consumerProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "group1")
    consumerProperties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    consumerProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    consumerProperties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    new KafkaConsumer[String, String](consumerProperties)
  }

  def consumeMessages(consumer: KafkaConsumer[String, String], topicName: String): Unit = {
    consumer.subscribe(java.util.Collections.singletonList(topicName))
    while (true) {
      val writer = new PrintWriter(new FileWriter("src/main/scala/reisaks/FinalProject/ServerSide/Kafka/KafkaLog.txt", true))
      val records = consumer.poll(1000L)
      for (record <- records.asScala) {
        writer.println(record.value())
        writer.flush()
      }
      writer.close()
    }
  }
  def startConsumer(): Unit = {
    val topicName = "Spinning-Wheel-Game-Round"
    val consumer = EventConsumer.initConsumer
    EventConsumer.consumeMessages(consumer, topicName)
  }
}

