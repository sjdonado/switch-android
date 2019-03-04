package com.example.juan.aswitch.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyQualify(
        val value: Double,
        val comment: String
) : Parcelable