package com.example.juan.aswitch.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
        val id: String?,
        val uid: String?,
        val email: String?,
        val location: Location?,
        val name: String?,
        val phoneNumber: String?,
        val profilePicture: ImageObject?,
        var radius: Int?,
        var filters: ArrayList<String>?,
        val role: Boolean?
) : Parcelable

@Parcelize
data class ImageObject(
        val ref: String?,
        val url: String?
) : Parcelable

@Parcelize
data class Location(
        val address: String,
        val lat: Double,
        val lng: Double,
        val viewport: Viewport
) : Parcelable

@Parcelize
data class Viewport(
        val northeast: LocationData,
        val southwest: LocationData
) : Parcelable

@Parcelize
data class LocationData(
        val lat: Double,
        val lng: Double
) : Parcelable