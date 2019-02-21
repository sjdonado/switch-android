package com.example.juan.aswitch.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class LocationData(
        val lat: Double,
        val lng: Double
) : Parcelable