package com.example.juan.aswitch.helpers

import okhttp3.*
import org.json.JSONObject
import okhttp3.FormBody

open class HttpClient {

    companion object {
//        http://10.0.2.2:8010/switch-dev-smartrends/us-central1/switchDev/api/v1
//        https://us-central1-switch-dev-smartrends.cloudfunctions.net/switchDev/api/v1
        const val API_URL = "http://10.0.2.2:8010/switch-dev-smartrends/us-central1/switchDev/api/v1"
        var TOKEN: String? = null

        fun get(path: String, callback: (response: JSONObject) -> Unit) {
            val request = Request.Builder()
                    .header("Authorization", TOKEN!!)
                    .url(API_URL + path)
                    .build()
            executeRequest(path, request, callback)
        }

        fun post(path: String, formBody: FormBody, callback: (response: JSONObject) -> Unit) {
            val request = Request.Builder()
                    .header("Authorization", TOKEN!!)
                    .url(API_URL + path)
                    .post(formBody)
                    .build()
            executeRequest(path, request, callback)
        }

        fun put(path: String, formBody: FormBody, callback: (response: JSONObject) -> Unit) {
            val request = Request.Builder()
                    .header("Authorization", TOKEN!!)
                    .url(API_URL + path)
                    .put(formBody)
                    .build()
            executeRequest(path, request, callback)
        }

        fun uploadImage(path : String, multipartBody: MultipartBody, callback: (response : JSONObject) -> Unit) {
            val request = Request.Builder()
                    .url(API_URL + path)
                    .header("Authorization", TOKEN!!)
                    .post(multipartBody)
                    .build()
            executeRequest(path, request, callback)
        }

        private fun executeRequest(path : String, request: Request, callback: (response: JSONObject) -> Unit) {
            Thread {
                val response = OkHttpClient().newCall(request).execute()
                val body = JSONObject(response.body()!!.string()).getJSONObject("data")
                callback(body)
            }.start()
        }
    }
}