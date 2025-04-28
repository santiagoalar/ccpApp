package com.example.ccpapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ccpapp.network.TokenManager
import com.example.ccpapp.repositories.PurchaseItemsRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class CartItemViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application.applicationContext)
    private val purchaseItemsRepository = PurchaseItemsRepository(application)
    private var _isNetworkErrorShown = MutableLiveData(false)
    private var _eventNetworkError = MutableLiveData(false)

    fun savePurchase(
        purchase: JSONObject
    ) {
        viewModelScope.launch {
            try {
                val token: String = tokenManager.getToken()
                purchaseItemsRepository.savePurchase(token, purchase)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CartItemViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CartItemViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError
}