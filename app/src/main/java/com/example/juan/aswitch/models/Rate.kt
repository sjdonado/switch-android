package com.example.juan.aswitch.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Rate(
        val qualify: Double?,
        val size: Int?
) : Parcelable