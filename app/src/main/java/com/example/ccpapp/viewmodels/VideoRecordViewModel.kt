package com.example.ccpapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccpapp.network.TokenManager
import com.example.ccpapp.repositories.VideoRecordIARepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class VideoRecordViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application.applicationContext)
    private val videoRecordIARepository = VideoRecordIARepository(application)

    fun sendVideo(videoFile: java.io.File) {
        val token = tokenManager.getToken()
        viewModelScope.launch {
            try {
                videoRecordIARepository.sendVideo(videoFile, token)
                JSONObject()
            } catch (e: Exception) {
                JSONObject()
            }

        }
    }

}