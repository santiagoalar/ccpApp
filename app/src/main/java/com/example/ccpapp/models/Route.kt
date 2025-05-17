package com.example.ccpapp.models

data class Route(
    val id: String,
    val name: String,
    val description: String,
    val createdAt: String,
    val updatedAt: String,
    val dueToDate: String,
    val userId: String,
    val zone: String,
    val waypoints: List<WayPoint>
)
