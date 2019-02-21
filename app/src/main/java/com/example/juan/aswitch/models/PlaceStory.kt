package com.example.juan.aswitch.models

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaceStory(
        val id: String,
        val profilePicture: ImageObject,
        val views: ArrayList<String>,
        val seconds: Int
) : Parcelable