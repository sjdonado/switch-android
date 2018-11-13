package com.example.juan.aswitch.helpers

import com.example.juan.aswitch.R
import android.app.Activity
import android.app.Dialog
import android.util.Log
import android.view.View
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import android.widget.ProgressBar
import android.widget.Toast
import okhttp3.RequestBody


open class HttpClient {

    companion object {
        private const val PROD_URl = "https://us-central1-switch-dev-smartrends.cloudfunctions.net"
        private const val DEV_URL = "http://10.0.2.2:8010/switch-dev-smartrends/us-central1"
        private const val API_URL = "$DEV_URL/switchDev/api/v1"
        private val JSON = MediaType.parse("application/json; charset=utf-8")

        fun get(path : String, activity : Activity, callback : (response : JSONObject) -> Unit) {
            val request = Request.Builder()
                    .header("Authorization", Functions.getSharedPreferencesValue(activity, "USER_TOKEN")!!)
                    .url(API_URL + path)
                    .build()
            executeRequest(request, activity, callback)
        }

        fun post(path : String, activity : Activity, json : String, callback : (response: JSONObject) -> Unit) {
            val jsonBody = RequestBody.create(JSON, json)
            val request = Request.Builder()
                    .header("Authorization", Functions.getSharedPreferencesValue(activity, "USER_TOKEN")!!)
                    .url(API_URL + path)
                    .post(jsonBody)
                    .build()
            executeRequest(request, activity, callback)
        }

        fun put(path : String, activity : Activity, json : String, callback : (response : JSONObject) -> Unit) {
            val jsonBody = RequestBody.create(JSON, json)
            val request = Request.Builder()
                    .header("Authorization", Functions.getSharedPreferencesValue(activity, "USER_TOKEN")!!)
                    .url(API_URL + path)
                    .put(jsonBody)
                    .build()
            executeRequest(request, activity, callback)
        }

        fun upload(path : String, activity : Activity, multipartBody : MultipartBody, callback : (response : JSONObject) -> Unit) {
            val request = Request.Builder()
                    .url(API_URL + path)
                    .header("Authorization", Functions.getSharedPreferencesValue(activity, "USER_TOKEN")!!)
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
                    if(response!!.code() == 200) {
                        val body = JSONObject(response.body()!!.string()).getJSONObject("data")
                        callback(body)
                    }else{
                        activity.runOnUiThread {
                            Toast.makeText(activity.applicationContext, activity.getString(R.string.internet_server_error), Toast.LENGTH_SHORT).show()
                        }
                        callback(JSONObject())
                    }

                }
            })
        }
    }
}