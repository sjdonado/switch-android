package com.example.juan.aswitch.services

import android.app.Activity
import com.example.juan.aswitch.helpers.HttpClient
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

    fun get(path: String, callback: (err: Boolean, response: JSONObject) -> Unit, loading: Boolean) {
        HttpClient.get(this.path + path, this.activity, callback, loading)
    }

    fun post(path: String, json: String, callback: (err: Boolean, response: JSONObject) -> Unit, loading: Boolean) {
        HttpClient.post(this.path + path, this.activity, json, callback, loading)
    }

    fun put(path: String, json: String, callback: (err: Boolean, response: JSONObject) -> Unit, loading: Boolean) {
        HttpClient.put(this.path + path, this.activity, json, callback, loading)
    }

    fun delete(path: String, json: String, callback: (err: Boolean, response: JSONObject) -> Unit, loading: Boolean) {
        HttpClient.delete(this.path + path, this.activity, json, callback, loading)
    }

    fun upload(path : String, multipartBody : MultipartBody, callback : (err: Boolean, response : JSONObject) -> Unit, loading: Boolean) {
        HttpClient.upload(this.path + path, this.activity, multipartBody, callback, loading)
    }

}