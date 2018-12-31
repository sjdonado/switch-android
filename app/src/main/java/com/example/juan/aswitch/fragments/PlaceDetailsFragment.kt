package com.example.juan.aswitch.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.Utils
import org.json.JSONObject
import kotlinx.android.synthetic.main.fragment_place_details.*
import android.content.Intent
import android.net.Uri
import com.bumptech.glide.request.RequestOptions


class PlaceDetailsFragment : androidx.fragment.app.Fragment() {

    private var placeObject: JSONObject = JSONObject()

    companion object {
        fun getInstance(): PlaceDetailsFragment = PlaceDetailsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_place_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userObjectValue = Utils.getSharedPreferencesStringValue(activity!!, "PLACE_OBJECT")
        if(userObjectValue != null) placeObject = JSONObject(userObjectValue)

        Glide.with(activity!!).load(placeObject.getString("imgUrl"))
                .apply(RequestOptions().placeholder(Utils.getCircularProgressDrawable(activity!!)))
                .into(placeDetailsCoverImageView)
        placeDetailsNameTextView.text = placeObject.getString("name")
        placeDetailsLocationTextView.text = placeObject.getString("address")
        placeDetailsPhoneTextView.text = placeObject.getString("phone")
        placeDetailsDistanceTextView.text = resources.getString(
                R.string.place_card_view_distance,
                placeObject.getString("distance")
        )

        placeDetailsGoButton.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q=${placeObject.getString("lat")},${placeObject.getString("lng")}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(activity!!.packageManager) != null) {
                startActivity(mapIntent)
            }
        }

        placeDetailsCallButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${placeObject.getString("phone")}")
            }
            if (intent.resolveActivity(activity!!.packageManager) != null) {
                startActivity(intent)
            }
        }

        Log.d("PLACE_OBJECT", "geo:${placeObject.getString("lat")},${placeObject.getString("lng")}")
    }
}
