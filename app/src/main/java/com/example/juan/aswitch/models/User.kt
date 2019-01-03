package com.example.juan.aswitch.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
        val uid: String?,
        val email: String?,
        val location: Location?,
        val name: String?,
        val phoneNumber: String?,
        val profilePicture: ProfilePicture?,
        val radius: Int?,
        val role: Boolean?
) : Parcelable

@Parcelize
data class ProfilePicture(
        val ref: String?,
        val url: String
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