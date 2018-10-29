package com.example.juan.aswitch.services

import android.util.Log
import com.example.juan.aswitch.helpers.HttpClient
import okhttp3.*
import org.json.JSONObject
import java.io.File

open class UserService {

    companion object {
        private const val PATH = "/users"

        fun get(path: String, callback: (response: JSONObject) -> Unit) {
            HttpClient.get(PATH + path, callback)
        }

        fun signUp(path: String, callback: (response: JSONObject) -> Unit) {
            HttpClient.post(PATH + path, FormBody.Builder().build(), callback)
        }

        fun post(path: String, jsonObject: JSONObject, callback: (response: JSONObject) -> Unit) {
            HttpClient.post(PATH + path, formBuilder(jsonObject), callback)
        }

        fun put(path: String, jsonObject: JSONObject, callback: (response: JSONObject) -> Unit) {
            HttpClient.put(PATH + path, formBuilder(jsonObject), callback)
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
            HttpClient.uploadImage(PATH + path, multipartBody, callback)
        }

        private fun formBuilder(jsonObject: JSONObject) : FormBody{
            return FormBody.Builder()
                    .add("name", jsonObject.getString("name"))
                    .add("email", jsonObject.getString("email"))
                    .build()
        }
    }
}