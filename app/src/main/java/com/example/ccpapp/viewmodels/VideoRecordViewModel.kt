package com.example.ccpapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ccpapp.adapters.ClientAdapter
import com.example.ccpapp.models.VideoResponse
import com.example.ccpapp.models.VideoStatus
import com.example.ccpapp.network.TokenManager
import com.example.ccpapp.repositories.VideoRecordIARepository
import kotlinx.coroutines.launch

class VideoRecordViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application.applicationContext)
    private val videoRecordIARepository = VideoRecordIARepository(application)

    private val _videoSendResult = MutableLiveData<VideoResponse?>()
    val videoSendResult: LiveData<VideoResponse?> = _videoSendResult

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _videoStatus = MutableLiveData<VideoStatus?>()
    val videoStatus: LiveData<VideoStatus?> = _videoStatus

    fun sendVideo(videoFile: java.io.File) {
        val token = tokenManager.getToken()
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = videoRecordIARepository.sendVideo(videoFile, token)
                _videoSendResult.value = result
                ClientAdapter.UserStorage.setVideoId(result.id)
                Log.d("VideoRecordViewModel", "Video enviado exitosamente: $result")
            } catch (e: Exception) {
                Log.e("VideoRecordViewModel", "Error al enviar video: ${e.message}", e)
                _videoSendResult.value = VideoResponse(
                    id = "",
                    fileName = "",
                    status = "error",
                    message = "Error al enviar video: ${e.message}"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkIfVideoFinished() {
        val token: String = tokenManager.getToken()
        val videoId: String = ClientAdapter.UserStorage.getVideoId() ?: return
        
        if (videoId.isEmpty()) return
        
        viewModelScope.launch {
            try {
                val result = videoRecordIARepository.checkIfVideoFinished(videoId, token)
                _videoStatus.value = result
                Log.d("VideoRecordViewModel", "Estado del video: $result")
            } catch (e: Exception) {
                Log.e(
                    "VideoRecordViewModel",
                    "Error al verificar estado del video: ${e.message}",
                    e
                )
                _videoStatus.value = VideoStatus(
                    createdAt = "",
                    id = "videoId",
                    status = "ERROR",
                    updatedAt = "Error al verificar: ${e.message}",
                    analysisResult = null
                )
            }
        }
    }
}
