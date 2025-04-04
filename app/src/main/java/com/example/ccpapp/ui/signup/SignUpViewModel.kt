package com.example.ccpapp.ui.signup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ccpapp.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class SignUpViewModel (application: Application) :  AndroidViewModel(application) {

    private val userRepository = UserRepository(application)

    private val _postUserResult = MutableLiveData<Boolean>()
    val postUserResult: LiveData<Boolean>
        get() = _postUserResult


    private var _eventNetworkError = MutableLiveData<Boolean>(false)

    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)

    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    init {
        refreshDataFromNetwork()
    }

    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    private fun refreshDataFromNetwork() {
        try {
            viewModelScope.launch (Dispatchers.Default){
                withContext(Dispatchers.IO){
                }
                _eventNetworkError.postValue(false)
                _isNetworkErrorShown.postValue(false)
            }
        }
        catch (e:Exception){
            _eventNetworkError.value = true
        }
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SignUpViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    fun postUser(userObject: JSONObject){
        viewModelScope.launch {
            try {
                userRepository.saveData(userObject)
                _postUserResult.value = true
            } catch (e: Exception){
                _postUserResult.value = false
            }
        }
    }
}