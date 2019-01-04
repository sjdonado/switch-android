package com.example.juan.aswitch.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Place(
        val id: String,
        val name: String,
        val distance: Double,
        val nit: String,
        val phoneNumber: String,
        val signboard: String,
        val profilePicture: ImageObject,
        val images: ArrayList<ImageObject>,
        val location: Location
) : Parcelable