package com.example.ccpapp.models

data class WayPoint(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val order: Int,
    val createdAt: String
)
