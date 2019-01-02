package com.example.juan.aswitch.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Place(
        val id: String,
        val name: String,
        val imgUrl: String,
        val address: String,
        val distance: Int,
        val phone: String,
        val lat: Double,
        val lng: Double
) : Parcelable