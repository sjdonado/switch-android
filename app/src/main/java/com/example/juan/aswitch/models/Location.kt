package com.example.juan.aswitch.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(
        val address: String,
        val lat: Double,
        val lng: Double,
        val viewport: Viewport
) : Parcelable