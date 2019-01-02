package com.example.juan.aswitch.services

import android.app.Activity
import org.json.JSONObject

open class UsersPlaceService (activity: Activity) : MainService("/users-places", activity) {

    fun accept(userId: String, placeId: String, callback: (response: JSONObject) -> Unit) {
        val jsonObject = JSONObject()
        jsonObject.put("userId", userId)
        jsonObject.put("placeId", placeId)
        super.post("/accept", jsonObject.toString(), callback)
    }

    fun reject(userId: String, placeId: String, callback: (response: JSONObject) -> Unit) {
        val jsonObject = JSONObject()
        jsonObject.put("userId", userId)
        jsonObject.put("placeId", placeId)
        super.post("/reject", jsonObject.toString(), callback)
    }
}