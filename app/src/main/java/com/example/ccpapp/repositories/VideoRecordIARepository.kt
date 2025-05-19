package com.example.ccpapp.repositories

import android.app.Application
import com.example.ccpapp.models.VideoResponse
import com.example.ccpapp.models.VideoStatus
import com.example.ccpapp.network.NetworkServiceAdapter
import org.json.JSONObject

class VideoRecordIARepository (private val application: Application) {

    suspend fun sendVideo(videoFile: java.io.File,token: String): VideoResponse {
        return NetworkServiceAdapter.getInstance(application).sendVideo(videoFile, token)
    }

    suspend fun checkIfVideoFinished(videoId: String, token: String): VideoStatus {
        return NetworkServiceAdapter.getInstance(application).checkIfVideoFinished(videoId, token)
    }


}