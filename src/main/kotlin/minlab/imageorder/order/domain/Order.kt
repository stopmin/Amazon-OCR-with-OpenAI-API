package minlab.imageorder.order.domain

import java.time.LocalDateTime

data class Order(
    val startLoadAddress: String,
    val endLoadAddress: String,
    val description: String,
    val startLoadTime: LocalDateTime,
    val endLoadTime: LocalDateTime
)
