package com.example.ccpapp.repositories

import android.app.Application
import com.example.ccpapp.models.TokenInfo
import com.example.ccpapp.models.User
import com.example.ccpapp.network.NetworkServiceAdapter
import org.json.JSONObject

class UserRepository(private val application: Application) {

    suspend fun saveData(user: JSONObject) {
        return NetworkServiceAdapter.getInstance(application).postUser(user)
    }

    suspend fun authUser(user: JSONObject): TokenInfo {
        return NetworkServiceAdapter.getInstance(application).authUser(user)
    }

    suspend fun validateToken(token: String): User {
        return NetworkServiceAdapter.getInstance(application).getUser(token)
    }

}