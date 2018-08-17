package com.cacuna.kafka.chapter_3

import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.Consumed
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.{KStream, Printed, Produced, ValueMapper}
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Properties


object KafkaStreamsYellingApp {
//  private val LOG = LoggerFactory.getLogger(classOf[KafkaStreamsYellingApp])

  @throws[Exception]
  def main(args: Array[String]): Unit = {
    //Used only to produce data for this application, not typical usage
//    MockDataProducer.produceRandomTextData()
    val props = new Properties
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "yelling_app_id")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    val streamsConfig = new StreamsConfig(props)
    val stringSerde: Serde[String] = Serdes.String
    val builder = new StreamsBuilder

    val simpleFirstStream: KStream[String, String] = builder.stream("src-topic", Consumed.`with`(stringSerde, stringSerde))
    val upperCasedStream = simpleFirstStream.mapValues(new ValueMapper[String, String] {
      override def apply(value: String): String = value.toUpperCase
    })

    upperCasedStream.to("out-topic")
    val kafkaStreams = new KafkaStreams(builder.build, streamsConfig)

    println("Hello World Yelling App Started")

    kafkaStreams.start()
    Thread.sleep(35000)
    println("Shutting down the Yelling APP now")
    kafkaStreams.close()

//    MockDataProducer.shutdown()
  }
}
