package com.example.ccpapp.repositories

import android.app.Application
import com.example.ccpapp.network.NetworkServiceAdapter
import org.json.JSONObject

class UserRepository(private val application: Application) {

    suspend fun saveData(user: JSONObject){
        return NetworkServiceAdapter.getInstance(application).postUser(user)
    }
}