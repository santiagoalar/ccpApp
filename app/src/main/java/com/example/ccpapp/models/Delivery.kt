package com.example.ccpapp.models

data class Delivery(
    val id: String,
    val orderId: String,
    val customerId: String,
    val sellerId: String,
    val description: String,
    val estimatedDeliveryDate: String,
    val createdAt: String,
    val statusUpdates: List<StatusUpdates>
)
