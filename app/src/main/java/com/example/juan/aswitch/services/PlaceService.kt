package com.example.juan.aswitch.services

import android.app.Activity
import org.json.JSONObject

open class PlaceService (activity: Activity) : MainService("/places", activity) {

    fun search(callback: (response: JSONObject) -> Unit) {
        super.get("/search", callback)
    }

    fun get(callback: (response: JSONObject) -> Unit) {
        super.get("/", callback)
    }

    fun update(jsonObject: JSONObject, callback: (response: JSONObject) -> Unit) {
        super.put("/", jsonObject.toString(), callback)
    }

    fun sendNotification(jsonObject: JSONObject, callback: (response: JSONObject) -> Unit) {
        super.post("/notification", jsonObject.toString(), callback)
    }
}