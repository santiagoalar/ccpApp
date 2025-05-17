package com.example.ccpapp.repositories

import android.app.Application
import com.example.ccpapp.models.Delivery
import com.example.ccpapp.models.Route
import com.example.ccpapp.network.NetworkServiceAdapter

class RouteRepository (private val application: Application) {

    suspend fun getAllRoutesByUser(userId: String, date: String, token: String): List<Route> {
        return NetworkServiceAdapter.getInstance(application).getAllRoutesByUserAndDate(userId, date, token)
    }
}