package com.example.ccpapp.models

data class VideoStatus(
    val id: String,
    val status: String, // PENDING, PROCESSING, COMPLETE, ERROR
    val analysisResult: String?,
    val updatedAt: String,
    val createdAt: String,
)
