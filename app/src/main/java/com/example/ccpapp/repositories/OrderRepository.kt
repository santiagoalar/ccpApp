package com.example.ccpapp.repositories

import android.app.Application
import com.example.ccpapp.models.Order
import com.example.ccpapp.network.NetworkServiceAdapter

class OrderRepository(private val application: Application) {

    suspend fun getAllOrders(token: String): List<Order> {
        return NetworkServiceAdapter.getInstance(application).getOrders(token)
    }


}