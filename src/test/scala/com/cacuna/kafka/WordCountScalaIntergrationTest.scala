package com.cacuna.kafka

import java.util.Properties

import net.manub.embeddedkafka.Codecs._
import net.manub.embeddedkafka.ConsumerExtensions._
import net.manub.embeddedkafka.{EmbeddedKafkaConfig, UUIDs}
import net.manub.embeddedkafka.streams.EmbeddedKafkaStreamsAllInOne
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, KafkaConsumer, OffsetResetStrategy}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, KTable}
import org.apache.kafka.common.serialization._
import org.apache.kafka.common.serialization.LongDeserializer
import org.apache.kafka.streams.kstream.Materialized
//import org.apache.kafka.streams.{KeyValue, StreamsBuilder}
//import org.apache.kafka.streams.kstream.{Consumed, KStream, Produced}
import org.scalatest.{Matchers, WordSpec}


class WordCountScalaIntergrationTest
  extends WordSpec
    with Matchers
    with EmbeddedKafkaStreamsAllInOne {

  import org.apache.kafka.streams.scala.Serdes.{ByteArray, Long, String}
  import org.apache.kafka.streams.scala.ImplicitConversions._

  import net.manub.embeddedkafka.Codecs.stringKeyValueCrDecoder

  implicit val config =
    EmbeddedKafkaConfig(kafkaPort = 7000, zooKeeperPort = 7001)

  org.apache.log4j.Logger.getLogger("kafka")

  val (inTopic, outTopic) = ("inputTopic", "output-topic")

  val stringSerde: Serde[String] = Serdes.String()

  "A Kafka streams test" should {
    "be easy to run with streams and consumer lifecycle management" in {
      val inputTextLines: Seq[String] = Seq(
        "Hello Kafka Streams",
        "All streams lead to Kafka",
        "Join Kafka Summit"
      )

      val expectedWordCounts: Seq[KeyValue[String, Long]] = Seq(
        ("hello", 1L),
        ("all", 1L),
        ("streams", 2L),
        ("lead", 1L),
        ("to", 1L),
        ("join", 1L),
        ("kafka", 3L),
        ("summit", 1L)
      )

      val builder = new StreamsBuilder

      val textLines: KStream[Array[Byte], String] = builder.stream[Array[Byte], String](inTopic)

      val wordCounts = textLines
        .flatMapValues(value => value.toLowerCase.split("\\W+"))
        .groupBy((_, word) => word)
        .count()(Materialized.as("counts-store"))

        wordCounts.toStream.to(outTopic)

//      val streamBuilder = new StreamsBuilder
//      val stream: KStream[String, String] =
//        streamBuilder.stream(inTopic, Consumed.`with`(stringSerde, stringSerde))
//
//      stream.to(outTopic, Produced.`with`(stringSerde, stringSerde))

      runStreams(Seq(inTopic, outTopic), builder.build()) {
        inputTextLines.foreach {line =>
          publishToKafka(inTopic, line)
        }

//        val consumer = newConsumer[String, LongDeserializer]()
//        consumer.consumeLazily[(String, Long)](outTopic).take(2) should be(
//          Seq("hello" -> "world", "foo" -> "bar"))


        implicit val longDeserailizer: Deserializer[java.lang.Long] = new LongDeserializer()

        val consumer = newConsumer[String, java.lang.Long]()
        consumer.consumeLazily(outTopic).take(1) should be (
          ("hello", 1L)
        )

//        val consumerConfig = {
//          val p = new Properties()
//          p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, s"localhost:${config.kafkaPort}")
//          p.put(ConsumerConfig.GROUP_ID_CONFIG, UUIDs.newUuid().toString)
//          p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.toString.toLowerCase)
//          p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer])
//          p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[LongDeserializer])
//          p
//        }

//        def withCustomConsumer[K: Deserializer, V: Deserializer, T](consumer: KafkaConsumer[K, V])(block: KafkaConsumer[K, V] => T): T = {
//          try {
//            val result = block(consumer)
//            result
//          } finally {
//            consumer.close()
//          }
//        }
//
//        val consumer = new KafkaConsumer[String, Long](consumerConfig)
//
//        consumer.consumeLazily(outTopic).take(1) should be("hello", 1L)
//
//        consumer.close()

//        withConsumer[String, Long, Unit] { consumer =>
//          val consumedMessages: Stream[(String, Long)] =
//            consumer.consumeLazily(outTopic)
//          consumedMessages.take(8) should be(expectedWordCounts)
//        }
      }
    }
  }
}

