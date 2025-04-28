package com.example.ccpapp.network

import android.content.Context
import com.example.ccpapp.models.TokenInfo
import androidx.core.content.edit

class TokenManager(context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveUserInfo(userInfo: TokenInfo) {
        prefs.edit() { putString("auth_token", userInfo.token) }
        prefs.edit() { putString("user_id", userInfo.id) }
    }

    fun getToken(): String {
        return prefs.getString("auth_token", "") ?: ""
    }

    fun clearToken() {
        prefs.edit().remove("auth_token").apply()
    }

    fun getUserId(): String {
        return prefs.getString("user_id", "") ?: ""
    }
}