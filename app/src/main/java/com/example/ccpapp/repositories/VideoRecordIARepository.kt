package com.example.ccpapp.repositories

import android.app.Application
import com.example.ccpapp.network.NetworkServiceAdapter
import org.json.JSONObject

class VideoRecordIARepository (private val application: Application) {

    suspend fun sendVideo(videoFile: java.io.File,token: String): JSONObject {
        return NetworkServiceAdapter.getInstance(application).sendVideo(videoFile, token)
    }


}