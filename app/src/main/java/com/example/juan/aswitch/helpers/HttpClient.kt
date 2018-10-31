package com.example.juan.aswitch.helpers

import android.app.Activity
import android.app.Dialog
import android.util.Log
import android.view.View
import okhttp3.*
import org.json.JSONObject
import okhttp3.FormBody
import java.io.IOException
import android.widget.ProgressBar



open class HttpClient {

    companion object {
//        http://10.0.2.2:8010/switch-dev-smartrends/us-central1/switchDev/api/v1
//        https://us-central1-switch-dev-smartrends.cloudfunctions.net/switchDev/api/v1
        const val API_URL = "https://us-central1-switch-dev-smartrends.cloudfunctions.net/switchDev/api/v1"
        var TOKEN: String? = null

        fun get(path : String, activity : Activity, callback : (response : JSONObject) -> Unit) {
            val request = Request.Builder()
                    .header("Authorization", TOKEN!!)
                    .url(API_URL + path)
                    .build()
            executeRequest(request, activity, callback)
        }

        fun post(path : String, activity : Activity, formBody : FormBody, callback : (response: JSONObject) -> Unit) {
            val request = Request.Builder()
                    .header("Authorization", TOKEN!!)
                    .url(API_URL + path)
                    .post(formBody)
                    .build()
            executeRequest(request, activity, callback)
        }

        fun put(path : String, activity : Activity, formBody : FormBody, callback : (response : JSONObject) -> Unit) {
            val request = Request.Builder()
                    .header("Authorization", TOKEN!!)
                    .url(API_URL + path)
                    .put(formBody)
                    .build()
            executeRequest(request, activity, callback)
        }

        fun upload(path : String, activity : Activity, multipartBody : MultipartBody, callback : (response : JSONObject) -> Unit) {
            val request = Request.Builder()
                    .url(API_URL + path)
                    .header("Authorization", TOKEN!!)
                    .post(multipartBody)
                    .build()
            executeRequest(request, activity, callback)
        }

        private fun executeRequest(request : Request, activity : Activity, callback : (response : JSONObject) -> Unit) {

            val progressDialog = Dialog(activity)
            val dialog = ProgressBar(activity)
            dialog.isIndeterminate = true
            dialog.visibility = View.VISIBLE

            progressDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            progressDialog.setContentView(dialog)
            progressDialog.show()

            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    Log.i("HTTP_RESPONSE", "${e?.message}")
                }

                override fun onResponse(call : Call?, response : Response?) {
                    activity.runOnUiThread {
                        progressDialog.hide()
                    }
                    val body = JSONObject(response?.body()?.string()).getJSONObject("data")
                    callback(body)
                }
            })
        }
    }
}