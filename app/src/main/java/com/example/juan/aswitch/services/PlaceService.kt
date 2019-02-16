package com.example.juan.aswitch.services

import android.app.Activity
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File

open class PlaceService (activity: Activity) : MainService("/places", activity) {

    fun search(radius: Int, categories: ArrayList<String>, filters: ArrayList<String>, callback: (response: JSONObject) -> Unit) {
        super.get("/search?radius=$radius&categories=${categories.joinToString(separator = ",")}&filters=${filters.joinToString(separator = ",")}", callback, true)
    }

    fun starredPlaces(callback: (response: JSONObject) -> Unit) {
        super.get("/starred", callback, true)
    }

    fun get(callback: (response: JSONObject) -> Unit) {
        super.get("/", callback, true)
    }

    fun getAllCategories(callback: (response: JSONObject) -> Unit) {
        super.get("/categories/all", callback, true)
    }

    fun update(jsonObject: JSONObject, callback: (response: JSONObject) -> Unit) {
        super.put("/", jsonObject.toString(), callback, true)
    }

    fun sendNotification(jsonObject: JSONObject, callback: (response: JSONObject) -> Unit) {
        super.post("/notification", jsonObject.toString(), callback, true)
    }

    fun uploadImage(position : String, image : File, callback: (response: JSONObject) -> Unit) {
        val mediaType = if (image.endsWith("png"))
            MediaType.parse("image/png")
        else
            MediaType.parse("image/jpeg")

        val multipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", image.name, RequestBody.create(mediaType, image))
                .build()
        super.upload("/image/$position", multipartBody, callback, true)
    }

    fun removeImage(jsonObject: JSONObject, callback: (response: JSONObject) -> Unit) {
        super.delete("/image", jsonObject.toString(), callback, true)
    }
}