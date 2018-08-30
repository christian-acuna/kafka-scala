package com.cacuna.kafka.confluent

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.StreamsConfig
import java.util.Properties

import org.apache.kafka.streams.scala.{Serdes, StreamsBuilder}

object SumLambdaExample {
  def main(args: Array[String]): Unit = {
    import Serdes._

    val NUMBERS_TOPIC = "numbers-topic"
    val SUM_OF_ODD_NUMBERS_TOPIC = "sum-of-odd-numbers-topic"
    val props: Properties = {
      val p = new Properties()
      p.put(StreamsConfig.APPLICATION_ID_CONFIG, "sum-lambda-example")
      p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
      p.put(StreamsConfig.CLIENT_ID_CONFIG, "sum-lambda-example-client")
      p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer.getClass.getName)
      p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Integer.getClass.getName)
      p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      p.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams")
      p.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000)
      p
    }

    val builder: StreamsBuilder = new StreamsBuilder
    val input: KStream[Int, Int] = builder.stream[Int, Int](NUMBERS_TOPIC)
    val sumOfOddNumbers = input
      .filter((_, value) => value % 2 != 0)
      .selectKey((key, value) => 1)
      .groupByKey
      .reduce(_ + _)

    sumOfOddNumbers.toStream.to(SUM_OF_ODD_NUMBERS_TOPIC)
  }
}
