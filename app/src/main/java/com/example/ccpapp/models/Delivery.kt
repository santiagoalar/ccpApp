package com.example.ccpapp.models

data class Delivery(
                    val id: String,
                    val name: String,
                    val arrivalDate: String,
                    val clientId: String,
                    val location: String,
                    val status: String,
                    val duration: String,
)
