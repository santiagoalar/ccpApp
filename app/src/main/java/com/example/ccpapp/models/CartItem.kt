package com.example.ccpapp.models

data class CartItem(
    val id: String,
    val characteristic: String,
    val quantity: Int,
    val unitPrice: Int,
    val totalPrice: Int
)