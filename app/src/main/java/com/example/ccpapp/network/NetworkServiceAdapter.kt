package com.example.ccpapp.network

import android.content.Context
import android.util.Log
import androidx.annotation.VisibleForTesting
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.ccpapp.constants.StaticConstants
import com.example.ccpapp.models.Client
import com.example.ccpapp.models.Delivery
import com.example.ccpapp.models.Order
import com.example.ccpapp.models.Product
import com.example.ccpapp.models.Rol
import com.example.ccpapp.models.Route
import com.example.ccpapp.models.StatusUpdates
import com.example.ccpapp.models.TokenInfo
import com.example.ccpapp.models.User
import com.example.ccpapp.models.VisitRecord
import com.example.ccpapp.models.WayPoint
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class NetworkServiceAdapter(context: Context) {

    companion object {
        private var instance: NetworkServiceAdapter? = null
        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: NetworkServiceAdapter(context).also {
                instance = it
            }
        }

        // Método para testing
        @VisibleForTesting
        fun setInstanceForTesting(mockInstance: NetworkServiceAdapter?) {
            instance = mockInstance
        }
    }

    private val appContext = context.applicationContext

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(appContext)
    }

    suspend fun postUser(userJson: JSONObject) = suspendCoroutine<Unit> { cont ->
        val url = "${StaticConstants.API_BASE_URL}users/"

        val request = JsonObjectRequest(
            Request.Method.POST, url, userJson,
            { response ->
                cont.resume(Unit)
                Log.d("response", "response successful")
            },
            { error ->
                cont.resumeWithException(error)
                Log.d("response failed", error.message.toString())
            }
        )

        Log.d("request", request.toString())
        requestQueue.add(request)
    }

    suspend fun authUser(userJson: JSONObject): TokenInfo = suspendCoroutine { cont ->
        val url = "${StaticConstants.API_BASE_URL}users/auth"

        val request = JsonObjectRequest(
            Request.Method.POST, url, userJson,
            { response ->
                try {
                    val token = response.getString("token")
                    val id = response.getString("id")
                    val expiresAt = response.getString("expiresAt")
                    val tokenInfo = TokenInfo(expiresAt = expiresAt, id = id, token = token)
                    cont.resume(tokenInfo)
                    Log.d("response", "response successful: $tokenInfo")
                } catch (e: Exception) {
                    cont.resumeWithException(e)
                }
            },
            { error ->
                cont.resumeWithException(error)
                Log.d("response failed", error.message.toString())
            }
        )

        requestQueue.add(request)
    }

    suspend fun getUser(token: String): User = suspendCoroutine { cont ->
        val url = "${StaticConstants.API_BASE_URL}users/me"

        val request = object : JsonObjectRequest(
            Method.GET, url, null,
            { response ->
                try {
                    val user = User(
                        id = response.getString("id"),
                        name = response.getString("name"),
                        email = response.getString("email"),
                        phone = response.getString("phone"),
                        password = null,
                        role = Rol.valueOf(response.getString("role").uppercase())
                    )
                    cont.resume(user)
                } catch (e: Exception) {
                    cont.resumeWithException(e)
                }
            },
            { error ->
                cont.resumeWithException(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        requestQueue.add(request)
    }

    suspend fun getProducts(token: String): List<Product> = suspendCoroutine { cont ->
        val url = StaticConstants.API_PRODUCT_BASE_URL
        val request = object : JsonArrayRequest(
            Method.GET, url, null,
            { response ->
                try {
                    val productList = parseProductList(response)
                    cont.resume(productList)
                } catch (e: Exception) {
                    cont.resumeWithException(e)
                }
            },
            { error ->
                cont.resume(emptyList())
                Log.d("Product API Error", error.message ?: "Error desconocido")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        requestQueue.add(request)
    }

    private fun parseProductList(jsonArray: JSONArray): List<Product> {
        val productList = mutableListOf<Product>()
        for (i in 0 until jsonArray.length()) {
            val product = jsonArray.getJSONObject(i)
            val detailsObject = product.getJSONObject("details")
            val imagesArray = product.getJSONArray("images")
            val images = mutableListOf<String>()
            for (i in 0 until imagesArray.length()) {
                images.add(imagesArray.getString(i))
            }
            productList.add(
                Product(
                    id = product.getString("id"),
                    name = product.getString("name"),
                    description = product.getString("description"),
                    details = detailsObject.keys().asSequence().associateWith { key ->
                        detailsObject.getString(key)
                    },
                    storageConditions = product.getString("storageConditions"),
                    price = product.getInt("price"),
                    deliveryTime = product.getInt("deliveryTime"),
                    images = images,
                    stock = product.getInt("stock"),
                    stockSelected = 0
                )
            )
        }
        return productList
    }

    private fun parseClientList(jsonArray: JSONArray): List<Client> {
        val clientList = mutableListOf<Client>()
        for (i in 0 until jsonArray.length()) {
            val client = jsonArray.getJSONObject(i)
            clientList.add(
                Client(
                    address = client.getString("address"),
                    city = client.getString("city"),
                    clientEmail = client.getString("clientEmail"),
                    clientId = client.getString("clientId"),
                    clientName = client.getString("clientName"),
                    clientPhone = client.getString("clientPhone"),
                    country = client.getString("country"),
                    id = client.getString("id"),
                    salesmanId = client.getString("salesmanId"),
                    storeName = client.getString("storeName")
                )
            )
        }
        return clientList
    }

    suspend fun getClients(token: String, sellerId: String): List<Client> =
        suspendCoroutine { cont ->
            val url =
                "${StaticConstants.API_BASE_URL}salesman/${sellerId}/clients"
            Log.d("RESPONSE", url.toString())
            val request = object : JsonArrayRequest(
                Method.GET, url, null, { response ->
                    try {
                        val clientList = parseClientList(response)
                        cont.resume(clientList)
                    } catch (e: Exception) {
                        cont.resumeWithException(e)
                    }
                },
                { error ->
                    cont.resume(emptyList())
                    Log.d("Client API Error", error.message ?: "Error desconocido")
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $token"
                    return headers
                }
            }
            requestQueue.add(request)
        }

    private fun readJsonFromAssets(context: Context, filename: String): String {
        return context.assets.open(filename).bufferedReader().use { it.readText() }
    }

    suspend fun postCartItems(token: String, cartItemsJson: JSONObject) =
        suspendCoroutine<Unit> { cont ->
            val url = "${StaticConstants.API_BASE_URL}clients/orders/"

            val request = object : JsonObjectRequest(
                Method.POST, url, cartItemsJson,
                { response ->
                    cont.resume(Unit)
                    Log.d("response", "Cart items posted successfully")
                },
                { error ->
                    cont.resumeWithException(error)
                    Log.d("response failed", error.message.toString())
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $token"
                    return headers
                }
            }

            Log.d("request", request.toString())
            requestQueue.add(request)
        }

    suspend fun getOrders(token: String, clientId: String): List<Order> = suspendCoroutine { cont ->
        val url = "${StaticConstants.API_BASE_URL}clients/orders?clientId=${clientId}"
        val request = object : JsonArrayRequest(
            Method.GET, url, null,
            { response ->
                try {
                    val orderList = parseOrderList(response)
                    cont.resume(orderList)
                } catch (e: Exception) {
                    cont.resumeWithException(e)
                }
            },
            { error ->
                try {
                    val fallbackJson = readJsonFromAssets(appContext, "orderJson.json")
                    val fallbackArray = JSONArray(fallbackJson)
                    val fallbackList = parseOrderList(fallbackArray)
                    cont.resume(fallbackList)
                } catch (ex: Exception) {
                    cont.resumeWithException(ex)
                }
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        requestQueue.add(request)
    }

    private fun parseOrderList(jsonArray: JSONArray): List<Order> {
        val orderList = mutableListOf<Order>()
        for (i in 0 until jsonArray.length()) {
            val product = jsonArray.getJSONObject(i)
            orderList.add(
                Order(
                    id = product.getString("id"),
                    clientId = product.getString("clientId"),
                    createdAt = product.getString("createdAt"),
                    currency = product.getString("currency"),
                    quantity = product.getString("quantity"),
                    status = product.getString("status"),
                    subtotal = product.getDouble("subtotal"),
                    tax = product.getDouble("tax"),
                    total = product.getDouble("total")
                )
            )
        }
        return orderList
    }

    suspend fun getDeliveries(userId: String, token: String): List<Delivery> =
        suspendCoroutine { cont ->
            Log.d("RESPONSE", userId)
            val url = "${StaticConstants.API_BASE_URL}deliveries/customers/${userId}"
            val request = object : JsonArrayRequest(
                Method.GET, url, null,
                { response ->
                    try {
                        val orderList = parseDeliveryList(response)
                        Log.d("DeliveryList", orderList.toString())
                        cont.resume(orderList)
                    } catch (e: Exception) {
                        cont.resumeWithException(e)
                    }
                },
                { error ->
                    cont.resume(emptyList())
                    Log.d("Delivery API Error", error.message ?: "Error desconocido")
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $token"
                    return headers
                }
            }
            requestQueue.add(request)
        }

    private fun parseDeliveryList(jsonArray: JSONArray): List<Delivery> {
        val deliveryList = mutableListOf<Delivery>()
        for (i in 0 until jsonArray.length()) {
            val delivery = jsonArray.getJSONObject(i)
            deliveryList.add(
                Delivery(
                    id = delivery.getString("id"),
                    orderId = delivery.getString("order_id"),
                    customerId = delivery.getString("customer_id"),
                    sellerId = delivery.getString("seller_id"),
                    description = delivery.getString("description"),
                    estimatedDeliveryDate = delivery.getString("estimated_delivery_date"),
                    createdAt = delivery.getString("created_at"),
                    statusUpdates = parseStatusUpdates(delivery.getJSONArray("status_updates"))
                )
            )
        }
        return deliveryList
    }

    private fun parseStatusUpdates(jsonArray: JSONArray): List<StatusUpdates> {
        val statusList = mutableListOf<StatusUpdates>()
        for (i in 0 until jsonArray.length()) {
            val status = jsonArray.getJSONObject(i)
            statusList.add(
                StatusUpdates(
                    id = status.getString("id"),
                    deliveryId = status.getString("delivery_id"),
                    status = status.getString("status"),
                    description = status.getString("description"),
                    createdAt = status.getString("created_at")
                )
            )
        }
        return statusList
    }

    suspend fun postVisit(visitJson: JSONObject, salesmanId: String, token: String) =
        suspendCoroutine<Unit> { cont ->
            val url = "${StaticConstants.API_BASE_URL}salesman/${salesmanId}/visits"
            val request = object : JsonObjectRequest(
                Method.POST, url, visitJson,
                { response ->
                    cont.resume(Unit)
                    Log.d("RESPONSE-POST-VISIT", "response successful")
                },
                { error ->
                    cont.resumeWithException(error)
                    Log.d("response failed", error.message?.toString() ?: "Unknown error")
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $token"
                    return headers
                }
            }

            Log.d("request", request.toString())
            requestQueue.add(request)
        }

    suspend fun getVisitRecords(token: String, salesmanId: String): List<VisitRecord> =
        suspendCoroutine { cont ->
            val url = "${StaticConstants.API_BASE_URL}salesman/${salesmanId}/visits"
            val request = object : JsonArrayRequest(
                Method.GET, url, null,
                { response ->
                    try {
                        val visitList = parseVisitRecordList(response)
                        cont.resume(visitList)
                    } catch (e: Exception) {
                        cont.resumeWithException(e)
                    }
                },
                { error ->
                    cont.resume(emptyList())
                    Log.d("VisitRecord API Error", error.message ?: "Error desconocido")
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $token"
                    return headers
                }
            }
            requestQueue.add(request)
        }

    private fun parseVisitRecordList(jsonArray: JSONArray): List<VisitRecord> {
        val visitList = mutableListOf<VisitRecord>()
        for (i in 0 until jsonArray.length()) {
            val product = jsonArray.getJSONObject(i)
            visitList.add(
                VisitRecord(
                    clientId = product.getString("clientId"),
                    notes = product.getString("notes"),
                    recordId = product.getString("recordId"),
                    salesmanId = product.getString("salesmanId"),
                    visitDate = product.getString("visitDate")
                )
            )
        }
        return visitList
    }

    suspend fun getAllRoutesByUserAndDate(
        userId: String,
        date: String,
        token: String
    ): List<Route> = suspendCoroutine { cont ->
        val url = "${StaticConstants.API_BASE_URL}routes/users/${userId}?due_to=${date}"
        val request = object : JsonArrayRequest(
            Method.GET, url, null,
            { response ->
                try {
                    val routeList = parseRouteList(response)
                    cont.resume(routeList)
                } catch (e: Exception) {
                    cont.resumeWithException(e)
                }
            },
            { error ->
                cont.resume(emptyList())
                Log.d("Delivery API Error", error.message ?: "Error desconocido")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        requestQueue.add(request)
    }

    private fun parseRouteList(jsonArray: JSONArray): List<Route> {
        val routeList = mutableListOf<Route>()
        for (i in 0 until jsonArray.length()) {
            val route = jsonArray.getJSONObject(i)
            routeList.add(
                Route(
                    id = route.getString("id"),
                    name = route.getString("name"),
                    description = route.getString("description"),
                    createdAt = route.getString("created_at"),
                    updatedAt = route.getString("updated_at"),
                    dueToDate = route.getString("due_to"),
                    userId = route.getString("user_id"),
                    zone = route.getString("zone"),
                    waypoints = route.getJSONArray("waypoints").let { waypointsArray ->
                        List(waypointsArray.length()) { index ->
                            val waypoint = waypointsArray.getJSONObject(index)
                            WayPoint(
                                id = waypoint.getString("id"),
                                name = waypoint.getString("name"),
                                latitude = waypoint.getDouble("latitude"),
                                longitude = waypoint.getDouble("longitude"),
                                address = waypoint.getString("address"),
                                order = waypoint.getInt("order"),
                                createdAt = waypoint.getString("created_at")
                            )
                        }
                    }
                )
            )
        }
        Log.d("RouteList", routeList.toString())
        return routeList
    }

    suspend fun sendVideo(videoFile: java.io.File, token: String): JSONObject =
        suspendCoroutine { cont ->
            val url = "${StaticConstants.API_BASE_URL}video/upload"
            val request = object : JsonObjectRequest(
                Method.POST, url, null,
                { response ->
                    cont.resume(response)
                    Log.d("response", "response successful")
                },
                { error ->
                    cont.resumeWithException(error)
                    Log.d("response failed", error.message.toString())
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $token"
                    return headers
                }
            }

            Log.d("request", request.toString())
            requestQueue.add(request)
        }

}
