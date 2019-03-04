package com.example.juan.aswitch.services

import android.app.Activity
import org.json.JSONObject

open class UsersPlaceService (activity: Activity) : MainService("/users-places", activity) {

    fun accept(userId: String, placeId: String, callback: (response: JSONObject) -> Unit) {
        val jsonObject = JSONObject()
        jsonObject.put("userId", userId)
        jsonObject.put("placeId", placeId)
        super.post("/accept", jsonObject.toString(), callback, false)
    }

    fun reject(userId: String, placeId: String, callback: (response: JSONObject) -> Unit) {
        val jsonObject = JSONObject()
        jsonObject.put("userId", userId)
        jsonObject.put("placeId", placeId)
        super.post("/reject", jsonObject.toString(), callback, false)
    }

    fun qualify(id: String, value: Float, comment: String, callback: (response: JSONObject) -> Unit) {
        val jsonObject = JSONObject()
        jsonObject.put("value", value)
        jsonObject.put("comment", comment)
        super.post("/qualify/$id", jsonObject.toString(), callback, true)
    }

    fun remove(id: String, callback: (response: JSONObject) -> Unit) {
        super.put("/remove/$id", JSONObject().toString(), callback, false)
    }

}