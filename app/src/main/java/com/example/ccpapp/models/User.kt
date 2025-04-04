package com.example.ccpapp.models

data class User(
    val id: Int,
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val role: Rol
)