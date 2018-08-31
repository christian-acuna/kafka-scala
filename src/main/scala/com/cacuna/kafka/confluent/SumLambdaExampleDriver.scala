package com.cacuna.kafka.confluent

import java.time.Duration

import com.cacuna.kafka.confluent.SumLambdaExample
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.stream.IntStream
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.Collections
import java.util.Properties

import scala.collection.JavaConversions._
import org.apache.kafka.streams.scala.Serdes


object SumLambdaExampleDriver {
  val SUM_OF_ODD_NUMBERS_TOPIC = "sum-of-odd-numbers-topic"
  val NUMBERS_TOPIC = "numbers-topic-2"
  def main(args: Array[String]): Unit = {
    produceInput("localhost:9092")
    consumeOutput("localhost:9092")
  }

  private def consumeOutput(bootstrapServers: String): Unit = {
    val properties = new Properties()
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Serdes.Integer.getClass.getName)
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Serdes.Integer.getClass.getName)
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, "sum-lambda-example-consumer")
    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    val consumer = new KafkaConsumer[_, _](properties)
    consumer.subscribe(Collections.singleton(SUM_OF_ODD_NUMBERS_TOPIC))
    val pollTimeout: Duration = Duration.ofHours(1)
    while ( {
      true
    }) {
      val records = consumer.poll(pollTimeout)
      for (record <- records) {
        System.out.println("Current sum of odd numbers is:" + record.value)
      }
    }
  }

  private def produceInput(bootstrapServers: String): Unit = {
    import Serdes._
    val properties = new Properties()
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,  Serdes.Integer.getClass.getName)
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,  Serdes.Integer.getClass.getName)
    val producer = new KafkaProducer[_, _](properties)
    IntStream.range(0, 100)
      .mapToObj((value: Int) => new ProducerRecord[Int, Int](NUMBERS_TOPIC, value, value))
      .forEach(record => producer.send(record))
    producer.flush()
  }
}
