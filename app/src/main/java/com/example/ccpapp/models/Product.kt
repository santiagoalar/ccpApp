package com.example.ccpapp.models

data class Product(
    val id: String,
    val characteristic: String,
    val description: String,
    val storageConditions: String,
    val deliveryTime: String,
    val imageUrl: String,
    val commercialConditions: String,
    val quantity: Int,
    val price: Int
)
