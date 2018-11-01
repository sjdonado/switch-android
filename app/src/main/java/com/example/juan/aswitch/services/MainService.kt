package com.example.juan.aswitch.services

import android.app.Activity
import android.view.View
import com.example.juan.aswitch.helpers.HttpClient
import okhttp3.FormBody
import okhttp3.MultipartBody
import org.json.JSONObject
import kotlin.properties.Delegates

open class MainService (path : String, activity: Activity){

    var path : String by Delegates.notNull()
    var activity : Activity by Delegates.notNull()

    init {
        this.path = path
        this.activity = activity
    }

    fun get(path: String, callback: (response: JSONObject) -> Unit) {
        HttpClient.get(this.path + path, this.activity, callback)
    }

    fun post(path: String, json: String, callback: (response: JSONObject) -> Unit) {
        HttpClient.post(this.path + path, this.activity, json, callback)
    }

    fun put(path: String, json: String, callback: (response: JSONObject) -> Unit) {
        HttpClient.put(this.path + path, this.activity, json, callback)
    }

    fun upload(path : String, multipartBody : MultipartBody, callback : (response : JSONObject) -> Unit) {
        HttpClient.upload(this.path + path, this.activity, multipartBody, callback)
    }

}