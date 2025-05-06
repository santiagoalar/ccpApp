package com.example.ccpapp.models

class Order(
    val id: String,
    val clientId: String,
    val createdAt: String,
    val currency: String,
    val quantity: String,
    val status: String,
    val subtotal: Double,
    val tax: Double,
    val total: Double,
)