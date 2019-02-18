package com.example.juan.aswitch.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Story(
        val path: String,
        val index: Int
) : Parcelable