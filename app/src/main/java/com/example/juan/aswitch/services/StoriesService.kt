package com.example.juan.aswitch.services

import android.app.Activity
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File

open class StoriesService (activity: Activity) : MainService("/stories", activity) {

    fun get(placeId: String, callback: (response: JSONObject) -> Unit) {
        super.get("/?place=$placeId", callback, false)
    }

    fun getAll(placeId: String, callback: (response: JSONObject) -> Unit) {
        super.get("/all?place=$placeId", callback, true)
    }

    fun viewStory(storyId: String, jsonObject: JSONObject, callback: (response: JSONObject) -> Unit) {
        super.post("/view/$storyId", jsonObject.toString(), callback, false)
    }

    fun delete(storyId: String, callback: (response: JSONObject) -> Unit) {
        super.delete("/$storyId", "", callback, true)
    }

    fun create(placeId: String, image: File, callback: (response: JSONObject) -> Unit) {
        val mediaType = if (image.endsWith("png"))
            MediaType.parse("image/png")
        else
            MediaType.parse("image/jpeg")

        val multipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", image.name, RequestBody.create(mediaType, image))
                .build()
        super.upload("/?place=$placeId", multipartBody, callback, false)
    }

}