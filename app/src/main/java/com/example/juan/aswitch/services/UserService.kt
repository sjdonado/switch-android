package com.example.juan.aswitch.services

import android.app.Activity
import android.view.View
import okhttp3.*
import org.json.JSONObject
import java.io.File

open class UserService (activity: Activity, progressBar: View) : MainService("/users", activity, progressBar) {

    fun getInfo(path: String, callback: (response: JSONObject) -> Unit) {
        super.get(path, callback)
    }

    fun signUp(path: String, callback: (response: JSONObject) -> Unit) {
        super.post(path, FormBody.Builder().build(), callback)
    }

    fun post(path: String, jsonObject: JSONObject, callback: (response: JSONObject) -> Unit) {
        val formBody = FormBody.Builder()
                .add("name", jsonObject.getString("name"))
                .add("email", jsonObject.getString("email"))
                .build()
        super.post(path, formBody, callback)
    }

    fun put(path: String, jsonObject: JSONObject, callback: (response: JSONObject) -> Unit) {
        val formBody = FormBody.Builder()
                .add("name", jsonObject.getString("name"))
                .add("email", jsonObject.getString("email"))
                .build()
        super.put(path, formBody, callback)
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