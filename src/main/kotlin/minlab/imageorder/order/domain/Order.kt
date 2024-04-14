package minlab.imageorder.order.domain

import java.time.LocalDateTime

data class Order(
    val startLoadAddress: String,
    val endLoadAddress: String,
    val description: String,
    val truckType: String,
    val truckWeight: String,
    val phoneNumber: String,
    val startLoadTime: LocalDateTime,
    val endLoadTime: LocalDateTime
)
