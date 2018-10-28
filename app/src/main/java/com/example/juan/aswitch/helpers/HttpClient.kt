package com.example.juan.aswitch.helpers

import android.util.Log
import co.metalab.asyncawait.async
import com.google.firebase.auth.FirebaseAuth
import okhttp3.*
import java.io.InputStream
import java.io.File
import org.json.JSONObject




open class HttpClient {

    companion object {
        const val API_URL = "https://us-central1-switch-dev-smartrends.cloudfunctions.net/switchDev/api/v1"
        var TOKEN : String = ""

        fun get (path : String, callback: (response : String) -> Unit) {
            val request = Request.Builder()
                    .header("Authorization", TOKEN)
                    .url(API_URL + path)
                    .build()
            executeRequest(path, request, callback)
        }

        fun uploadImage(path : String, field : String, image : File, callback: (response : String) -> Unit) {
            val MEDIA_TYPE = if (image.endsWith("png"))
                MediaType.parse("image/png")
            else
                MediaType.parse("image/jpeg")

            val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(field, image.name, RequestBody.create(MEDIA_TYPE, image))
                    .build()

            val request = Request.Builder()
                    .url(API_URL + path)
                    .header("Authorization", TOKEN)
                    .post(requestBody)
                    .build()
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