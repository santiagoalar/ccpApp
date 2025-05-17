package com.example.ccpapp.models

data class StatusUpdates(
    val id: String,
    val deliveryId: String,
    val status: String,
    val description: String,
    val createdAt: String
)
