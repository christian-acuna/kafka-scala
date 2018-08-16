package com.cacuna.kafka.chapter_2.partitioner

import com.cacuna.kafka.models.PurchaseKey
import org.apache.kafka.clients.producer.internals.DefaultPartitioner
import org.apache.kafka.common.Cluster


class PurchaseKeyPartitioner extends DefaultPartitioner {
  override def partition(topic: String, key: Any, keyBytes: Array[Byte], value: Any, valueBytes: Array[Byte], cluster: Cluster): Int = {
    var newKey = key
    var newKeyBytes = keyBytes

    if (key != null) {
      val purchaseKey = key.asInstanceOf[PurchaseKey]
      newKey = purchaseKey.getCustomerId
      newKeyBytes = key.asInstanceOf[String].getBytes
    }
    super.partition(topic, newKey, newKeyBytes, value, valueBytes, cluster)
  }
}
