package com.example.juan.aswitch.fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.Utils
import kotlinx.android.synthetic.main.fragment_place_details.*
import android.content.Intent
import android.net.Uri
import com.bumptech.glide.request.RequestOptions
import com.example.juan.aswitch.models.Place


class PlaceDetailsFragment : androidx.fragment.app.Fragment() {

    lateinit var place: Place

    companion object {
        fun getInstance(place: Place) = PlaceDetailsFragment().apply {
            arguments = Bundle().apply {
                putParcelable("PLACE", place)
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getParcelable<Place>("PLACE")?.let {
            place = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_place_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val size = Utils.getGlideSize(activity!!)
        Glide.with(activity!!).load(place.profilePicture.url)
                .apply(RequestOptions().placeholder(Utils.getCircularProgressDrawable(activity!!)))
                .apply(RequestOptions().override(size, size))
                .into(placeDetailsCoverImageView)

        placeDetailsNameTextView.text = place.name
        placeDetailsLocationTextView.text = place.location.address
        placeDetailsPhoneTextView.text = place.phoneNumber
        placeDetailsDistanceTextView.text = resources.getString(
                R.string.place_card_view_distance,
                Utils.getRoundedDistance(place.distance)
        )

        placeDetailsGoButton.setOnClickListener {
            val gmmIntentUri = Uri.parse(
                    "google.navigation:q=${place.location.lat},${place.location.lng}"
            )
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(activity!!.packageManager) != null) {
                startActivity(mapIntent)
            }
        }

        placeDetailsCallButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${place.phoneNumber}")
            }
            if (intent.resolveActivity(activity!!.packageManager) != null) {
                startActivity(intent)
            }
        }

        Log.d("PLACE_OBJECT", "geo:${place.location.lat},${place.location.lng}")
    }
}
