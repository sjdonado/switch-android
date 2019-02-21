package com.example.juan.aswitch.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Time(
        val hourOfDay: Int?,
        val minute: Int?
) : Parcelable