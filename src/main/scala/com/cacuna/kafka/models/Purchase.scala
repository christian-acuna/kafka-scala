package com.cacuna.kafka.models

import java.util.Date

case class Purchase(firstName: String,
                    lastName: String,
                    customerId: String,
                    creditCardNumber: String,
                    itemPurchased: String,
                    quantity: Int,
                    price: Double,
                    purchaseDate: Date,
                    zipCode: String,
                    department: String,
                    employeeId: String,
                    storeId: String)
