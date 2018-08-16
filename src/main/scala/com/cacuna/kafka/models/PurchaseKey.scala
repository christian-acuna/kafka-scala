package com.cacuna.kafka.models

import java.util.Date

class PurchaseKey(var customerId: String, var transactionDate: Date) {
  def getCustomerId: String = customerId

  def getTransactionDate: Date = transactionDate
}