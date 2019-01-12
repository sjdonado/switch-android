package com.example.juan.aswitch.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Place(
        val id: String,
        val userPlaceId: String?,
        val name: String,
        val distance: Double,
        val nit: String,
        val phoneNumber: String,
        val signboard: String,
        val openingTime: Time?,
        val closingTime: Time?,
        val images: ArrayList<ImageObject>,
        val location: Location,
        val description: String,
        val category: String,
        var qualify: Double?,
        var rate: Rate?,
        var profilePicture: ImageObject
) : Parcelable

@Parcelize
data class Time(
        val hourOfDay: Int?,
        val minute: Int?
) : Parcelable

@Parcelize
data class Rate(
        val qualify: Double?,
        val size: Int?
) : Parcelable