package com.example.juan.aswitch.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlin.collections.ArrayList

@Parcelize
data class Place(
        val id: String,
        val userPlaceId: String?,
        val name: String,
        val distance: Double,
        val nit: String,
        val phoneNumber: String,
        val signboard: String,
        val openingTime: Time?,
        val closingTime: Time?,
        var images: ArrayList<ImageObject>,
        val location: Location,
        val description: String,
        val category: String,
        var myQualify: MyQualify?,
        var rate: Rate?,
        var profilePicture: ImageObject,
        var stories: ArrayList<PlaceStory>?,
        var downloadedStoriesIndex: Int?
) : Parcelable