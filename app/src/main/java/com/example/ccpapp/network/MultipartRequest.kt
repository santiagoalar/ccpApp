package com.example.ccpapp.network

import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import org.json.JSONObject
import java.io.*

class MultipartRequest(
    method: Int,
    url: String,
    private val file: File,
    private val paramName: String,
    private val mimeType: String,
    private val listener: Response.Listener<JSONObject>,
    private val errorListener: Response.ErrorListener,
    private val bearerToken: String? = null
) : Request<JSONObject>(method, url, errorListener) {

    private val twoHyphens = "--"
    private val lineEnd = "\r\n"
    private val boundary = "apiclient-" + System.currentTimeMillis()

    override fun getHeaders(): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        headers["Content-Type"] = "multipart/form-data; boundary=$boundary"
        if (bearerToken != null) {
            headers["Authorization"] = "Bearer $bearerToken"
        }
        return headers
    }

    override fun getBodyContentType(): String {
        return "multipart/form-data; boundary=$boundary"
    }

    @Throws(AuthFailureError::class)
    override fun getBody(): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val dataOutputStream = DataOutputStream(byteArrayOutputStream)

        try {
            // Agregar encabezado del archivo
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd)
            dataOutputStream.writeBytes(
                "Content-Disposition: form-data; name=\"$paramName\"; filename=\"${file.name}\"$lineEnd"
            )
            dataOutputStream.writeBytes("Content-Type: $mimeType$lineEnd")
            dataOutputStream.writeBytes(lineEnd)

            // Leer y escribir el contenido del archivo
            val fileInputStream = FileInputStream(file)
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
                dataOutputStream.write(buffer, 0, bytesRead)
            }
            fileInputStream.close()
            
            dataOutputStream.writeBytes(lineEnd)

            // Finalizar el multipart request
            dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)

            return byteArrayOutputStream.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
            return ByteArray(0)
        } finally {
            try {
                dataOutputStream.flush()
                dataOutputStream.close()
                byteArrayOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject> {
        return try {
            val jsonString = String(response.data, charset(HttpHeaderParser.parseCharset(response.headers)))
            Response.success(JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: Exception) {
            Response.error(ParseError(e))
        }
    }

    override fun deliverResponse(response: JSONObject) {
        listener.onResponse(response)
    }
}
