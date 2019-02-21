package com.example.juan.aswitch.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Viewport(
        val northeast: LocationData,
        val southwest: LocationData
) : Parcelable