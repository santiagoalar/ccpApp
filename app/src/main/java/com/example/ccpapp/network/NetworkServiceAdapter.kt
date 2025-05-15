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
import com.example.ccpapp.models.Delivery
import com.example.ccpapp.models.Order
import com.example.ccpapp.models.Product
import com.example.ccpapp.models.Rol
import com.example.ccpapp.models.TokenInfo
import com.example.ccpapp.models.User
import com.example.ccpapp.models.VisitRecord
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
                try {
                    val fallbackJson = readJsonFromAssets(appContext, "product2Json.json")
                    val fallbackArray = JSONArray(fallbackJson)
                    val fallbackList = parseProductList(fallbackArray)
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
                    storageConditions = product.getString("storage_conditions"),
                    price = product.getInt("price"),
                    deliveryTime = product.getInt("delivery_time"),
                    images = images,
                    stock = product.getInt("stock"),
                    stockSelected = 0
                )
            )
        }
        return productList
    }

    private fun parseClientList(jsonArray: JSONArray): List<User> {
        val clientList = mutableListOf<User>()
        for (i in 0 until jsonArray.length()) {
            val client = jsonArray.getJSONObject(i)
            clientList.add(
                User(
                    id = client.getString("id"),
                    name = client.getString("name"),
                    phone = client.getString("phone"),
                    email = client.getString("email"),
                    password = "",
                    role = Rol.CLIENTE
                )
            )
        }
        return clientList
    }

    suspend fun getClients(token: String, sellerId: String): List<User> = suspendCoroutine { cont ->
        val url =
            "${StaticConstants.API_BASE_URL}users/role/CLIENTE"
        Log.d("RESPONSE", url.toString())
        val request = object : JsonArrayRequest(
            Method.GET, url, null, { response ->
                try {
                    Log.d("RESPONSE", response.toString())
                    val clientList = parseClientList(response)
                    cont.resume(clientList)
                } catch (e: Exception) {
                    try {
                        val fallbackJson = readJsonFromAssets(appContext, "clientsJson.json")
                        val fallbackArray = JSONArray(fallbackJson)
                        val fallbackList = parseClientList(fallbackArray)
                        Log.d("SELLER ", fallbackList.size.toString())
                        cont.resume(fallbackList)
                    } catch (ex: Exception) {
                        cont.resumeWithException(ex)
                    }
                }
            },
            { error ->
                try {
                    val fallbackJson = readJsonFromAssets(appContext, "clientsJson.json")
                    val fallbackArray = JSONArray(fallbackJson)
                    val fallbackList = parseClientList(fallbackArray)
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

    suspend fun getDeliveries(token: String): List<Delivery> = suspendCoroutine { cont ->
        val url = "${StaticConstants.API_BASE_URL}clients/delivery/"
        val request = object : JsonArrayRequest(
            Method.GET, url, null,
            { response ->
                try {
                    val orderList = parseDeliveryList(response)
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
            val product = jsonArray.getJSONObject(i)
            deliveryList.add(
                Delivery(
                    id = product.getString("id"),
                    name = product.getString("name"),
                    arrivalDate = product.getString("arrivalDate"),
                    location = product.getString("location"),
                    clientId = product.getString("clientId"),
                    status = product.getString("status"),
                    duration = product.getString("duration"),
                )
            )
        }
        return deliveryList
    }

    suspend fun postVisit(visitJson: JSONObject, salesmanId: String, token: String) = suspendCoroutine<Unit> { cont ->
        val url = "${StaticConstants.API_BASE_URL}salesman/${salesmanId}/visits"

        val request = object : JsonObjectRequest(
            Method.POST, url, visitJson,
            { response ->
                cont.resume(Unit)
                Log.d("response", "response successful")
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

    suspend fun getVisitRecords(token: String, salesmanId: String): List<VisitRecord> = suspendCoroutine { cont ->
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


}
