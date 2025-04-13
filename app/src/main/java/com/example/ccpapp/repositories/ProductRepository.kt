package com.example.ccpapp.repositories

import android.app.Application
import com.example.ccpapp.models.Product
import com.example.ccpapp.network.NetworkServiceAdapter

class ProductRepository(private val application: Application) {

    suspend fun getAllProducts(token: String): List<Product> {
        return NetworkServiceAdapter.getInstance(application).getProducts(token)
    }
}