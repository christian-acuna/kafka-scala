package com.cacuna.kafka.confluent

import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.json4s._
import org.json4s.native.JsonMethods._

//bin/kafka-console-producer.sh --topic plaintext-input --broker-list localhost:9092

//bin/kafka-console-consumer.sh --topic wordcount-output --from-beginning \
//--bootstrap-server localhost:9092 \
//--property print.key=true \
//--property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

//{"@timestamp":"2018-05-11T17:08:32.882+00:00",
// "@version":1,
// "message":"",
// "logger_name":"node-events",
// "thread_name":"application-akka.actor.default-dispatcher-43",
// "level":"INFO",
// "level_value":20000,
// "application.home":"/srv/www/nodelogging/releases/20180510234746",
// "boot_count":"33",
// "session_id":196316,
// "request_count":null,
// "network_id":83387,
// "application":"node",
// "eero_id":778815,
// "user_id":null,
// "admin_id":null,
// "organization_id":null,
// "initiator_time":1526058512882,
// "time":1526058512882,
// "event":"node.event",
// "log_version":1,
// "firmware":"v3.8.0-1170+2018-05-07.stage.unico",
// "details":{
// "value":83742720,
// "timestamp":1526057699323,
// "name":"sys.memory.memory.slab_unrecl",
// "source":"collectd",
// "boot_count":33,
// "firmware":"v3.8.0-1170+2018-05-07.stage.unico",
// "model":"unico",
// "_id":"0a695654-553c-11e8-8021-4b42754dc006",
// "internet":true,
// "role":"leaf",
// "net_size":3,
// "_netid":83387,
// "_groupid":0,
// "_orgid":null}
// }

object NodeEventApp {
  def main(args: Array[String]): Unit = {
    import Serdes._

    val props: Properties = {
      val p = new Properties()
      p.put(StreamsConfig.APPLICATION_ID_CONFIG, "node-event-example")
      p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
      p.put(StreamsConfig.CLIENT_ID_CONFIG, "node-event-example-client")
//      p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
//      p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
      p
    }

    val builder: StreamsBuilder = new StreamsBuilder
    val eventInputStream: KStream[Array[Byte], String] = builder
      .stream[Array[Byte], String]("event-input")

    val eventCount= eventInputStream
      .map{ (_, value) =>
        println(value)
        val parsed = parse(value)
        val nodeIds: List[BigInt] = for {
          JObject(event) <- parsed
          JField("eero_id", JInt(id)) <- event
        } yield id

        println(nodeIds.head.toString)

        (nodeIds.head.toString, value)
      }
      .groupBy((id, _) => id)
      .count()(Materialized.as("counts-store"))

    eventCount.toStream.to("event-output")

    val streams: KafkaStreams = new KafkaStreams(builder.build(), props)
    streams.start()

    sys.ShutdownHookThread {
      streams.close(10, TimeUnit.SECONDS)
    }
  }
}
