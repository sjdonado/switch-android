package com.example.juan.aswitch.services

import android.app.Activity
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File

open class PlaceService (activity: Activity) : MainService("/places", activity) {

    fun search(radius: Int, categories: ArrayList<String>, filters: ArrayList<String>, callback: (err: Boolean, response: JSONObject) -> Unit) {
        super.get("/search?radius=$radius&categories=${categories.joinToString(separator = ",")}&filters=${filters.joinToString(separator = ",")}", callback, true)
    }

    fun starredPlaces(callback: (err: Boolean, response: JSONObject) -> Unit) {
        super.get("/starred", callback, true)
    }

    fun get(callback: (err: Boolean, response: JSONObject) -> Unit) {
        super.get("/", callback, true)
    }

    fun getAllCategories(callback: (err: Boolean, response: JSONObject) -> Unit) {
        super.get("/categories/all", callback, true)
    }

    fun update(jsonObject: JSONObject, callback: (err: Boolean, response: JSONObject) -> Unit) {
        super.put("/", jsonObject.toString(), callback, true)
    }

    fun sendNotification(jsonObject: JSONObject, callback: (err: Boolean, response: JSONObject) -> Unit) {
        super.post("/notification", jsonObject.toString(), callback, true)
    }

    fun uploadImage(position : String, image : File, callback: (err: Boolean, response: JSONObject) -> Unit) {
        super.upload("/image/$position", uploadMedia(position, image), callback, true)
    }

    fun uploadStory(position : String, image : File, callback: (err: Boolean, response: JSONObject) -> Unit) {
        super.upload("/story/$position", uploadMedia(position, image), callback, true)
    }

    fun removeImage(jsonObject: JSONObject, callback: (err: Boolean, response: JSONObject) -> Unit) {
        super.delete("/image", jsonObject.toString(), callback, true)
    }

    fun removeStory(jsonObject: JSONObject, callback: (err: Boolean, response: JSONObject) -> Unit) {
        super.delete("/story", jsonObject.toString(), callback, true)
    }

    private fun uploadMedia(position : String, image : File): MultipartBody {
        val mediaType = if (image.endsWith("png"))
            MediaType.parse("image/png")
        else
            MediaType.parse("image/jpeg")

        return MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", image.name, RequestBody.create(mediaType, image))
                .build()
    }
}