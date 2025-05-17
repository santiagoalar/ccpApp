package com.example.ccpapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ccpapp.models.Route
import com.example.ccpapp.network.TokenManager
import com.example.ccpapp.repositories.RouteRepository
import kotlinx.coroutines.launch

class RouteViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application.applicationContext)
    private val routeRepository = RouteRepository(application)
    private var _eventNetworkError = MutableLiveData<Boolean>(false)
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    private val _routes = MutableLiveData<List<Route>?>()
    private val _selectedRoute = MutableLiveData<Route>()
    val selectedRoute: LiveData<Route> = _selectedRoute


    fun setSelectedRoute(route: Route) {
        _selectedRoute.value = route
    }

    val routes: MutableLiveData<List<Route>?>
        get() = _routes

    val isNetworkErrorShown: MutableLiveData<Boolean>
        get() = _isNetworkErrorShown

    fun loadRoutes() {
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken()
                val userId = tokenManager.getUserId()
                val routes =
                    routeRepository.getAllRoutesByUser(userId = userId, date = "", token = token)
                _routes.value = routes
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
            } catch (e: Exception) {
                _eventNetworkError.value = true
            }
        }
    }


}