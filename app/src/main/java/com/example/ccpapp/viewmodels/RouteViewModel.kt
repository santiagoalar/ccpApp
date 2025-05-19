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
    private val _selectedDate = MutableLiveData<String>()
    
    val selectedRoute: LiveData<Route> = _selectedRoute
    val selectedDate: LiveData<String> = _selectedDate


    fun setSelectedRoute(route: Route) {
        // Ordenar los waypoints por el atributo order antes de establecer la ruta seleccionada
        val sortedWaypoints = route.waypoints.sortedBy { it.order }
        val routeWithSortedWaypoints = route.copy(waypoints = sortedWaypoints)
        _selectedRoute.value = routeWithSortedWaypoints
    }
    
    fun setSelectedDate(date: String) {
        _selectedDate.value = date
        loadRoutesByDate(date)
    }

    val routes: MutableLiveData<List<Route>?>
        get() = _routes

    val isNetworkErrorShown: MutableLiveData<Boolean>
        get() = _isNetworkErrorShown

    fun loadRoutes() {
        loadRoutesByDate("")
    }
    
    private fun loadRoutesByDate(date: String) {
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken()
                val userId = tokenManager.getUserId()
                val routes =
                    routeRepository.getAllRoutesByUser(userId = userId, date = date, token = token)

                val routesWithSortedWaypoints = routes.map { route ->
                    val sortedWaypoints = route.waypoints.sortedBy { it.order }
                    route.copy(waypoints = sortedWaypoints)
                }

                _routes.value = routesWithSortedWaypoints
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
            } catch (e: Exception) {
                _eventNetworkError.value = true
            }
        }
    }
}

