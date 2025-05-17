package com.example.ccpapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ccpapp.models.VisitRecord
import com.example.ccpapp.network.TokenManager
import com.example.ccpapp.repositories.VisitRecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

open class VisitRecordsViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenManager = TokenManager(application.applicationContext)
    private val visitRecordsRepository = VisitRecordRepository(application)
    private var _eventNetworkError = MutableLiveData<Boolean>(false)
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    private val _visitRecords = MutableLiveData<List<VisitRecord>?>()

    val visitRecords: MutableLiveData<List<VisitRecord>?>
        get() = _visitRecords

    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError


    fun saveVisitRecord(visitJson: JSONObject) {
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken()
                val salesmanId = tokenManager.getUserId()

                val apiVisitJson = JSONObject().apply {
                    put("visitDate", visitJson.getString("visitDate"))
                    put("notes", visitJson.getString("notes"))
                    put("clientId", visitJson.getString("clientId"))
                }
                visitRecordsRepository.saveData(apiVisitJson, salesmanId, token)

                loadVisitRecords()
            } catch (e: Exception) {
                e.printStackTrace()
                _eventNetworkError.postValue(true)
            }
        }
    }


    fun loadVisitRecords() {
        try {
            viewModelScope.launch(Dispatchers.Default) {
                withContext(Dispatchers.IO) {
                    val token = tokenManager.getToken() ?: ""
                    val salesmanId = tokenManager.getUserId()
                    val visitRecords = visitRecordsRepository.getAllVisitRecords(token, salesmanId)
                    _visitRecords.postValue(visitRecords)
                }
                _eventNetworkError.postValue(false)
                _isNetworkErrorShown.postValue(false)
            }
        } catch (e: Exception) {
            _eventNetworkError.value = true
        }
    }

    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VisitRecordsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return VisitRecordsViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
