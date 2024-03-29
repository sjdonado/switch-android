package com.example.juan.aswitch.models

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Story(
        val id: String,
        val image: Bitmap,
        val views: ArrayList<String>,
        val seconds: Int
) : Parcelable