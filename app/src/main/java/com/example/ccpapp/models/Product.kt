package com.example.ccpapp.models

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val details: ProductDetail,
    val storageConditions: String,
    val price: Int,
    val deliveryTime: Int,
    val images: List<String>,
    val stock: Int,
    val stockSelected: Int
) {

}
