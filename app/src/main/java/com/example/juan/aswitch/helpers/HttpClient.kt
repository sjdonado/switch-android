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
        private const val TAG = "HttpClient"
        private const val PROD_URl = "https://us-central1-switch-dev-smartrends.cloudfunctions.net"
        private const val DEV_URL = "http://10.0.2.2:8010/switch-dev-smartrends/us-central1"
        private const val API_URL = "$DEV_URL/switchDev/api/v1"
        private val JSON = MediaType.parse("application/json; charset=utf-8")

        fun get(path : String, activity : Activity, callback : (response : JSONObject) -> Unit, loading: Boolean) {
            val request = Request.Builder()
                    .header("Authorization", Utils.getSharedPreferencesStringValue(activity, "USER_TOKEN")!!)
                    .url(API_URL + path)
                    .build()
            executeRequest(request, activity, callback, loading)
        }

        fun post(path : String, activity : Activity, json : String, callback : (response: JSONObject) -> Unit, loading: Boolean) {
            val jsonBody = RequestBody.create(JSON, json)
            val request = Request.Builder()
                    .header("Authorization", Utils.getSharedPreferencesStringValue(activity, "USER_TOKEN")!!)
                    .url(API_URL + path)
                    .post(jsonBody)
                    .build()
            executeRequest(request, activity, callback, loading)
        }

        fun put(path : String, activity : Activity, json : String, callback : (response : JSONObject) -> Unit, loading: Boolean) {
            val jsonBody = RequestBody.create(JSON, json)
            val request = Request.Builder()
                    .header("Authorization", Utils.getSharedPreferencesStringValue(activity, "USER_TOKEN")!!)
                    .url(API_URL + path)
                    .put(jsonBody)
                    .build()
            executeRequest(request, activity, callback, loading)
        }

        fun delete(path : String, activity : Activity, json : String, callback : (response: JSONObject) -> Unit, loading: Boolean) {
            val jsonBody = RequestBody.create(JSON, json)
            val request = Request.Builder()
                    .header("Authorization", Utils.getSharedPreferencesStringValue(activity, "USER_TOKEN")!!)
                    .url(API_URL + path)
                    .delete(jsonBody)
                    .build()
            executeRequest(request, activity, callback, loading)
        }


        fun upload(path : String, activity : Activity, multipartBody : MultipartBody, callback : (response : JSONObject) -> Unit, loading: Boolean) {
            val request = Request.Builder()
                    .url(API_URL + path)
                    .header("Authorization", Utils.getSharedPreferencesStringValue(activity, "USER_TOKEN")!!)
                    .post(multipartBody)
                    .build()
            executeRequest(request, activity, callback, loading)
        }

        private fun executeRequest(request: Request, activity: Activity, callback : (response : JSONObject) -> Unit, loading: Boolean) {
            var progressDialog = Dialog(activity)
//             has leaked window DecorView@d3d464c[] that was originally added here -> SERVER DOESN'T SEND DATA OBJECT
            if (loading) {
                try {
                    progressDialog = Utils.showLoading(activity)
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                    callback(JSONObject())
                }
            }

            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    Log.i(TAG, "${e?.message}")
                    activity.runOnUiThread {
                        Toast.makeText(activity.applicationContext, activity.getString(R.string.internet_no_internet_connection), Toast.LENGTH_LONG).show()
                    }
                }
                override fun onResponse(call : Call?, response : Response?) {
                    if (loading) {
                        activity.runOnUiThread {
                            progressDialog.hide()
                        }
                    }
                    if(response!!.code() == 200) {
                        callback(JSONObject(response.body()!!.string()))
                    }else{
                        Log.i(TAG, "${response.code()}")
                        activity.runOnUiThread {
                            Toast.makeText(activity.applicationContext, activity.getString(R.string.internet_server_error), Toast.LENGTH_LONG).show()
                        }
                        callback(JSONObject())
                    }

                }
            })
        }
    }
}