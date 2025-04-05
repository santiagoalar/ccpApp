package com.example.ccpapp.network

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.ccpapp.constants.StaticConstants
import com.example.ccpapp.models.TokenInfo
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

    private val requestQueue: RequestQueue by lazy {
        // applicationContext keeps you from leaking the Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
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


}