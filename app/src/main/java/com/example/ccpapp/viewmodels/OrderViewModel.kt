package com.example.ccpapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ccpapp.models.Order
import com.example.ccpapp.network.TokenManager
import com.example.ccpapp.repositories.OrderRepository
import kotlinx.coroutines.launch

class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application.applicationContext)
    private val orderRepository = OrderRepository(application)
    private var _eventNetworkError = MutableLiveData<Boolean>(false)
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    private val _orders = MutableLiveData<List<Order>?>()

    val orders: MutableLiveData<List<Order>?>
        get() = _orders

    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    fun loadOrders() {
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken()
                val orders = orderRepository.getAllOrders(token)
                _orders.value = orders
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