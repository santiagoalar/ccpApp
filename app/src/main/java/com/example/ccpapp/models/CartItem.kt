package com.example.ccpapp.models

data class CartItem(
    val id: String,
    val name: String,
    var quantity: Int,
    val unitPrice: Int,
    var totalPrice: Int,
    val maxStock: Int,
)