package com.example.juan.aswitch.helpers

import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.File
import okhttp3.FormBody

open class HttpClient {

    companion object {

        const val API_URL = "http://10.0.2.2:8010/switch-dev-smartrends/us-central1/switchDev/api/v1"
        var TOKEN: String? = null

        fun get(path : String, callback: (response : String) -> Unit) {
            val request = Request.Builder()
                    .header("Authorization", TOKEN!!)
                    .url(API_URL + path)
                    .build()
            executeRequest(path, request, callback)
        }

        fun post(path : String, body: JSONObject, callback: (response : String) -> Unit) {
            val requestBody = FormBody.Builder()
                    .build()

            val request = Request.Builder()
                    .header("Authorization", TOKEN!!)
                    .url(API_URL + path)
                    .post(requestBody)
                    .build()
            executeRequest(path, request, callback)
        }

        fun put(path : String, body: JSONObject, callback: (response : String) -> Unit) {
            val requestBody = FormBody.Builder()
                    .add("name", body.getString("name"))
                    .build()

            val request = Request.Builder()
                    .header("Authorization", TOKEN!!)
                    .url(API_URL + path)
                    .put(requestBody)
                    .build()
            executeRequest(path, request, callback)
        }

        fun uploadImage(path : String, field : String, image : File, callback: (response : String) -> Unit) {
            val MEDIA_TYPE = if (image.endsWith("png"))
                MediaType.parse("image/png")
            else
                MediaType.parse("image/jpeg")

            Log.i("MEDIA_TYPE", MEDIA_TYPE.toString())

            val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(field, image.name, RequestBody.create(MEDIA_TYPE, image))
                    .build()

            val request = Request.Builder()
                    .url(API_URL + path)
                    .header("Authorization", TOKEN!!)
                    .post(requestBody)
                    .build()
            Log.i("REQUEST_TO_SEND", request.toString())
            executeRequest(path, request, callback)
        }

        private fun executeRequest(path : String, request: Request, callback: (response: String) -> Unit) {
            Thread {
                val response = OkHttpClient().newCall(request).execute()
                val body = response.body()!!.string()
                callback(body)
            }.start()
        }
    }
}