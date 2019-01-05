package com.example.juan.aswitch.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Place(
        val id: String,
        val userPlaceId: String?,
        val name: String,
        val distance: Double,
        val nit: String,
        val phoneNumber: String,
        val signboard: String,
        val images: ArrayList<ImageObject>,
        val location: Location,
        val description: String,
        val category: String,
        var qualify: Double?,
        var rate: Double?,
        var profilePicture: ImageObject
) : Parcelable