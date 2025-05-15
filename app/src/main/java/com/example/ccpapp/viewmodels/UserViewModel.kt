package com.example.ccpapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ccpapp.models.Client
import com.example.ccpapp.models.TokenInfo
import com.example.ccpapp.models.User
import com.example.ccpapp.network.TokenManager
import com.example.ccpapp.repositories.ClientRepository
import com.example.ccpapp.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application.applicationContext)
    private val userRepository = UserRepository(application)
    private val clientRepository = ClientRepository(application)

    private val _clients = MutableLiveData<List<Client>>()
    private val _postUserResult = MutableLiveData<Boolean>()
    private val _authUserResult = MutableLiveData<TokenInfo>()
    private val _tokenUserResult = MutableLiveData<User?>()
    private val _selectedClient = MutableLiveData<Client>()

    val clients: LiveData<List<Client>>
        get() = _clients

    val postUserResult: LiveData<Boolean>
        get() = _postUserResult

    val authUserResult: LiveData<TokenInfo>
        get() = _authUserResult

    val tokenUserResult: MutableLiveData<User?>
        get() = _tokenUserResult

    val selectedClient: LiveData<Client>
        get() = _selectedClient

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
            viewModelScope.launch(Dispatchers.Default) {
                withContext(Dispatchers.IO) {
                }
                _eventNetworkError.postValue(false)
                _isNetworkErrorShown.postValue(false)
            }
        } catch (e: Exception) {
            _eventNetworkError.value = true
        }
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    fun postUser(userObject: JSONObject) {
        viewModelScope.launch {
            try {
                userRepository.saveData(userObject)
                _postUserResult.value = true
            } catch (e: Exception) {
                _postUserResult.value = false
            }
        }
    }

    fun authenticateUser(userObject: JSONObject) {
        viewModelScope.launch {
            try {
                val tokenInfo = userRepository.authUser(userObject)
                tokenManager.saveUserInfo(tokenInfo)
                _authUserResult.value = tokenInfo
            } catch (e: Exception) {
                _authUserResult.value = TokenInfo("", "", "")
            }
        }
    }

    fun validateToken(token: String) {
        viewModelScope.launch {
            try {
                val userInfo = userRepository.validateToken(token)
                _tokenUserResult.value = userInfo
            } catch (e: Exception) {
                _tokenUserResult.value = null
            }
        }
    }

    fun refreshClients() {
        viewModelScope.launch {
            val token: String = tokenManager.getToken()
            try {
                val userId = tokenManager.getUserId()
                val clientsList = clientRepository.getAllClients(token, userId)
                _clients.value = clientsList
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
            } catch (e: Exception) {
                _eventNetworkError.value = true
            }
        }
    }

    fun setSelectedClient(client: Client) {
        _selectedClient.value = client
    }

}
