package com.example.ccpapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ccpapp.models.Product
import com.example.ccpapp.network.NetworkServiceAdapter
import com.example.ccpapp.network.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SellerClientProductViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application.applicationContext)

    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _selectedProducts = MutableLiveData<MutableList<Product>>(mutableListOf())
    val selectedProducts: LiveData<MutableList<Product>> = _selectedProducts

    private val networkServiceAdapter = NetworkServiceAdapter.getInstance(application)

    fun getProducts() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val productList = withContext(Dispatchers.IO) {
                    //networkServiceAdapter.getProducts()
                }
                //_products.value = productList
            } catch (e: Exception) {
                // Manejar error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addProductToSelection(product: Product) {
        val currentList = _selectedProducts.value ?: mutableListOf()
        if (!currentList.contains(product)) {
            currentList.add(product)
            _selectedProducts.value = currentList
        }
    }

    fun removeProductFromSelection(product: Product) {
        val currentList = _selectedProducts.value ?: mutableListOf()
        currentList.remove(product)
        _selectedProducts.value = currentList
    }

    fun clearSelectedProducts() {
        _selectedProducts.value = mutableListOf()
    }
}
