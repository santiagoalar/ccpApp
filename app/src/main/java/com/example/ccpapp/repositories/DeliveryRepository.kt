package com.example.ccpapp.repositories

import android.app.Application
import com.example.ccpapp.models.Delivery
import com.example.ccpapp.network.NetworkServiceAdapter

class DeliveryRepository(private val application: Application) {

    suspend fun getAllDeliveries(token: String): List<Delivery> {
        return return NetworkServiceAdapter.getInstance(application).getDeliveries(token)
    }
}