package com.example.ccpapp.models

data class User(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val password: String?,
    val role: Rol
)