package com.example.juan.aswitch.services

import android.app.Activity
import android.view.View
import okhttp3.*
import org.json.JSONObject
import java.io.File

open class UserService (activity: Activity) : MainService("/users", activity) {

    fun getInfo(callback: (response: JSONObject) -> Unit) {
        super.get("/", callback)
    }

    fun sendNotification(jsonObject: JSONObject, callback: (response: JSONObject) -> Unit) {
        super.post("/notification", jsonObject.toString(), callback)
    }

    fun updateUser(jsonObject: JSONObject, callback: (response: JSONObject) -> Unit) {
        super.put("/", jsonObject.toString(), callback)
    }

    fun uploadImage(path : String, field : String, image : File, callback: (response : JSONObject) -> Unit) {
        val mediaType = if (image.endsWith("png"))
            MediaType.parse("image/png")
        else
            MediaType.parse("image/jpeg")

        val multipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(field, image.name, RequestBody.create(mediaType, image))
                .build()
        super.upload(path, multipartBody, callback)
    }
}