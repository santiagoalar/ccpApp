package com.example.ccpapp.network

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.ccpapp.constants.StaticConstants
import com.example.ccpapp.models.Product
import com.example.ccpapp.models.Rol
import com.example.ccpapp.models.TokenInfo
import com.example.ccpapp.models.User
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

        val request = object : JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val user = User(
                        id = response.getString("id"),
                        name = response.getString("name"),
                        email = response.getString("email"),
                        phone = response.getString("email"),
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
        val url = "${StaticConstants.API_BASE_URL}products"

        val request = object : JsonObjectRequest(Method.GET, url, null, { response ->
            try {
                val productJson = JSONArray(response)
                val productList = parseProductList(productJson)
                cont.resume(productList)
            } catch (e: Exception) {
                try {
                    val fallbackJson = readJsonFromAssets(appContext, "productJson.json")
                    val fallbackArray = JSONArray(fallbackJson)
                    val fallbackList = parseProductList(fallbackArray)
                    cont.resume(fallbackList)
                } catch (ex: Exception) {
                    cont.resumeWithException(ex)
                }
            }
        },
            { error ->
                try {
                    val fallbackJson = readJsonFromAssets(appContext, "productJson.json")
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
            productList.add(
                Product(
                    id = product.getString("id"),
                    characteristic = product.getString("characteristic"),
                    description = product.getString("description"),
                    storageConditions = product.getString("storageConditions"),
                    deliveryTime = product.getString("deliveryTime"),
                    imageUrl = product.getString("imageUrl"),
                    commercialConditions = product.getString("commercialConditions"),
                    quantity = product.getInt("quantity"),
                    price = product.getInt("price")
                )

            )
        }
        return productList
    }

    private fun readJsonFromAssets(context: Context, filename: String): String {
        return context.assets.open(filename).bufferedReader().use { it.readText() }
    }
}
