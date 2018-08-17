//package com.cacuna.kafka.chapter_2.consumer
//
//import org.apache.kafka.clients.consumer.KafkaConsumer
//import java.util.Collections
//import java.util.Properties
//import java.util.concurrent.{ExecutorService, Executors, TimeUnit}
//
//object ThreadedConsumer {
//  /**
//    * Change the constructor arg to match the actual number of partitions
//    */
//  @throws[InterruptedException]
//  def main(args: Array[String]): Unit = {
//    val consumerExample = ThreadedConsumer(2)
//    consumerExample.startConsuming()
//    Thread.sleep(60000) //Run for one minute
//
//    consumerExample.stopConsuming()
//  }
//}
//
//case class ThreadedConsumer(var numberPartitions: Int) {
//  private var doneConsuming = false
//  private var executorService = Option.empty[ExecutorService]
//
//  def startConsuming(): Unit = {
//    executorService = Some(Executors.newFixedThreadPool(numberPartitions))
//    val properties = getConsumerProps
//
//    for (i <- 1 to numberPartitions) {
//      println(i)
//
//      val consumerThread = getConsumerThread(properties)
//      if (executorService.isDefined) {
//        executorService.get.submit(consumerThread)
//      }
//    }
//  }
//
//  private def getConsumerThread(properties: Properties) = new Runnable {
//    override def run() = {
//      var consumer = Option.empty[KafkaConsumer[String, String]]
//      try {
//        consumer = Some(new KafkaConsumer[String, String](properties))
//        if (consumer.isDefined) {
//          consumer.get.subscribe(Collections.singletonList("test-topic"))
//          while ( {
//            !doneConsuming
//          }) {
//            val records = consumer.get.poll(5000)
//            import scala.collection.JavaConversions._
//            for (record <- records) {
//              val message = String.format(
//                "Consumed: key = %s value = %s with offset = %d partition = %d",
//                record.key,
//                record.value,
//                record.offset,
//                record.partition
//              )
//
//              System.out.println(message)
//            }
//          }
//        }
//      } catch {
//        case e: Exception =>
//          e.printStackTrace()
//      } finally {
//        if (consumer.isDefined) {
//          consumer.get.close()
//        }
//      }
//    }
//  }
//
//
//  @throws[InterruptedException]
//  def stopConsuming(): Unit = {
//    doneConsuming = true
//    if (executorService.isDefined) {
//      executorService.get.awaitTermination(10000, TimeUnit.MILLISECONDS)
//      executorService.get.shutdownNow
//    }
//  }
//
//  private def getConsumerProps = {
//    val properties = new Properties
//    properties.put("bootstrap.servers", "localhost:9092")
//    properties.put("group.id", "simple-consumer-example")
//    properties.put("auto.offset.reset", "earliest")
//    properties.put("enable.auto.commit", "true")
//    properties.put("auto.commit.interval.ms", "3000")
//    properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
//    properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
//    properties
//  }
//}
//
