package com.example.juan.aswitch.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageObject(
        val ref: String?,
        val url: String?
) : Parcelable