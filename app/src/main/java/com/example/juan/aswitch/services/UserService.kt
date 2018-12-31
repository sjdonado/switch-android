package com.example.juan.aswitch.services

import android.app.Activity
import okhttp3.*
import org.json.JSONObject
import java.io.File

open class UserService (activity: Activity) : MainService("/users", activity) {

    fun get(callback: (response: JSONObject) -> Unit) {
        super.get("/", callback)
    }

    fun update(jsonObject: JSONObject, callback: (response: JSONObject) -> Unit) {
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