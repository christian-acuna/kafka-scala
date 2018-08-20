package com.cacuna.kafka.confluent

import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

//bin/kafka-console-producer.sh --topic plaintext-input --broker-list localhost:9092

//bin/kafka-console-consumer.sh --topic wordcount-output --from-beginning \
//--bootstrap-server localhost:9092 \
//--property print.key=true \
//--property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

object WordCountExample {
  def main(args: Array[String]): Unit = {
    import Serdes._

    val props: Properties = {
      val p = new Properties()
      p.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-lambda-example")
      p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
//      p.put(StreamsConfig.CLIENT_ID_CONFIG, "wordcount-lambda-example-client")
//      p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
//      p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
      p
    }

    val builder: StreamsBuilder = new StreamsBuilder
    val textLines: KStream[String, String] = builder.stream[String, String]("plaintext-input")

    val wordCounts: KTable[String, Long] = textLines
      .flatMapValues(textLine => textLine.toLowerCase.split("\\W+"))
      .groupBy((_, word) => word)
      .count()(Materialized.as("counts-store"))

    wordCounts.toStream.to("wordcount-output")

    val streams: KafkaStreams = new KafkaStreams(builder.build(), props)
    streams.start()

    sys.ShutdownHookThread {
      streams.close(10, TimeUnit.SECONDS)
    }
  }
}
