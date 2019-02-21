package com.example.juan.aswitch.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
        val id: String?,
        val uid: String?,
        val email: String?,
        val location: Location?,
        val name: String?,
        val phoneNumber: String?,
        val profilePicture: ImageObject?,
        var radius: Int?,
        var categories: ArrayList<String>?,
        var filters: ArrayList<String>?,
        val role: Boolean?
) : Parcelable