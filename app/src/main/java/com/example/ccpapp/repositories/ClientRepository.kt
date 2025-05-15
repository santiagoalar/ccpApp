package com.example.ccpapp.repositories

import android.app.Application
import com.example.ccpapp.models.Client
import com.example.ccpapp.network.NetworkServiceAdapter

class ClientRepository(private val application: Application) {

    suspend fun getAllClients(token: String, sellerId: String): List<Client> {
        return NetworkServiceAdapter.getInstance(application).getClients(token, sellerId)
    }
}