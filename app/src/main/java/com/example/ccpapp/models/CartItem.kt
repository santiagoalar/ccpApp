package com.example.ccpapp.models

data class CartItem(
    val id: String,
    val name: String,
    val quantity: Int,
    val unitPrice: Int,
    val totalPrice: Int
)