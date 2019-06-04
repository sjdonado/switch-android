package com.example.juan.aswitch.services

import android.app.Activity
import okhttp3.*
import org.json.JSONObject
import java.io.File

open class UserService (activity: Activity) : MainService("/users", activity) {

    fun get(loading: Boolean, callback: (err: Boolean, response: JSONObject) -> Unit) {
        super.get("/", callback, loading)
    }

    fun update(jsonObject: JSONObject, callback: (err: Boolean, response: JSONObject) -> Unit) {
        super.put("/", jsonObject.toString(), callback, true)
    }

    fun uploadImage(field : String, image : File, callback: (err: Boolean, response : JSONObject) -> Unit) {
        val mediaType = if (image.endsWith("png"))
            MediaType.parse("image/png")
        else
            MediaType.parse("image/jpeg")

        val multipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(field, image.name, RequestBody.create(mediaType, image))
                .build()
        super.upload("/upload", multipartBody, callback, true)
    }
}