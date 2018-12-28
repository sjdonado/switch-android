package com.example.juan.aswitch.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Place(val name: String, val url: String, val age: Int, val location: String) : Parcelable