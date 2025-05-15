package com.example.ccpapp.repositories

import android.app.Application
import com.example.ccpapp.models.VisitRecord
import com.example.ccpapp.network.NetworkServiceAdapter
import org.json.JSONObject

class VisitRecordRepository (private val application: Application) {

    suspend fun saveData(visit: JSONObject, salesmanId: String, token: String) {
        return NetworkServiceAdapter.getInstance(application).postVisit(visit, salesmanId, token)
    }

    suspend fun getAllVisitRecords(token: String, salesmanId: String): List<VisitRecord> {
        return NetworkServiceAdapter.getInstance(application).getVisitRecords(token, salesmanId)
    }
}