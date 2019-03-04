package com.example.juan.aswitch.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Rate(
        val value: Double,
        val size: Int,
        val comments: ArrayList<Comment>
) : Parcelable