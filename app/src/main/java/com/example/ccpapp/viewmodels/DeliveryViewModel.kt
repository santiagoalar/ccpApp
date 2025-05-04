package com.example.ccpapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ccpapp.models.Delivery
import com.example.ccpapp.network.TokenManager
import com.example.ccpapp.repositories.DeliveryRepository
import kotlinx.coroutines.launch

class DeliveryViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application.applicationContext)
    private val deliveryRepository = DeliveryRepository(application)
    private var _eventNetworkError = MutableLiveData<Boolean>(false)
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    private val _deliveries = MutableLiveData<List<Delivery>?>()

    val deliveries: MutableLiveData<List<Delivery>?>
        get() = _deliveries

    val isNetworkErrorShown: MutableLiveData<Boolean>
        get() = _isNetworkErrorShown

    fun loadDeliveries() {
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken()
                val deliveries = deliveryRepository.getAllDeliveries(token)
                _deliveries.value = deliveries
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
            } catch (e: Exception) {
                _eventNetworkError.value = true
            }
        }
    }

    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }
}