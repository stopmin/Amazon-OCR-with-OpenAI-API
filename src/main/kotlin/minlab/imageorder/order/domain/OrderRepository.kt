package minlab.imageorder.order.domain

import org.springframework.stereotype.Repository

@Repository
class OrderRepository {
    private val orders = mutableListOf<Order>()

    fun save(order: Order) {
        orders.add(order)
    }
}
