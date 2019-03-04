package com.example.juan.aswitch.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comment(
        val profilePicture: ImageObject,
        val name: String,
        val qualify: Double,
        val value: String
) : Parcelable