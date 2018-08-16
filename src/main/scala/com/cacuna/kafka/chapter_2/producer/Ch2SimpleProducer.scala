package com.cacuna.kafka.chapter_2.producer

import java.util.concurrent.Future
import java.util.{Date, Properties}

import com.cacuna.kafka.chapter_2.partitioner.PurchaseKeyPartitioner
import com.cacuna.kafka.models.PurchaseKey
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

import scala.util.Random

object Ch2SimpleProducer extends App {
  val events = args(0).toInt
  val topic = args(1)
  val rnd = new Random()
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("acks", "1")
  props.put("retries", "3")
  props.put("compression.type", "snappy")

  //This line in for demonstration purposes
  props.put("partitioner.class", classOf[PurchaseKeyPartitioner].getName)

  val key = new PurchaseKey("12334568", new Date)

  try {
    val producer = new KafkaProducer[PurchaseKey, String](props)
    val record = new ProducerRecord[PurchaseKey, String]("some-topic", key, "value")
    val callback = new Callback {
      override def onCompletion(recordMetadata: RecordMetadata, exception: Exception): Unit = {
        if (exception != null) exception.printStackTrace()
      }
    }

    producer.send(record, callback)
  }
}
//  val producer = new KafkaProducer[String, String](props)
//  val t = System.currentTimeMillis()
//  for (nEvents <- Range(0, events)) {
//    val runtime = new Date().getTime()
//    val ip = "192.168.2." + rnd.nextInt(255)
//    val msg = runtime + "," + nEvents + ",www.example.com," + ip
//    val data = new ProducerRecord[String, String](topic, ip, msg)
//
//    //async
////    producer.send(data, (m,e) => {})
//    //sync
//    producer.send(data)
//  }
//
//  System.out.println("sent per second: " + events * 1000 / (System.currentTimeMillis() - t))
//  producer.close()
