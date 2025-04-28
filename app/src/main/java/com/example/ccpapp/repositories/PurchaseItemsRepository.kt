package com.example.ccpapp.repositories

import android.app.Application
import com.example.ccpapp.network.NetworkServiceAdapter
import org.json.JSONObject


class PurchaseItemsRepository(private val application: Application) {

    suspend fun savePurchase(token: String, purchase: JSONObject) {
        return NetworkServiceAdapter.getInstance(application).postCartItems(token, purchase)
    }
}