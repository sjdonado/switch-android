package com.example.juan.aswitch.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.models.Place
import com.example.juan.aswitch.services.PlaceService
import kotlinx.android.synthetic.main.fragment_place.*


class PlaceFragment : androidx.fragment.app.Fragment() {

    private lateinit var placeService: PlaceService
    private lateinit var place: Place

    companion object {
        fun getInstance(): PlaceFragment = PlaceFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        placeService = PlaceService(activity!!)

        placeService.get {
            place = Utils.parseJSONPlace(it.getJSONObject("data"))
            activity!!.runOnUiThread {

                Glide.with(activity!!).load(place.profilePicture.url)
                        .apply(RequestOptions().placeholder(Utils.getCircularProgressDrawable(activity!!)))
                        .apply(RequestOptions().error(R.drawable.ic_image_white_grey_24dp))
                        .into(placeCoverImageView)

                Glide.with(activity!!).load(place.profilePicture.ref)
                        .apply(RequestOptions().placeholder(Utils.getCircularProgressDrawable(activity!!)))
                        .apply(RequestOptions().error(R.drawable.ic_image_white_grey_24dp))
                        .into(placeFirstImageView)

                Glide.with(activity!!).load(place.profilePicture.ref)
                        .apply(RequestOptions().placeholder(Utils.getCircularProgressDrawable(activity!!)))
                        .apply(RequestOptions().error(R.drawable.ic_image_white_grey_24dp))
                        .into(placeSecondImageView)

                Glide.with(activity!!).load(place.profilePicture.ref)
                        .apply(RequestOptions().placeholder(Utils.getCircularProgressDrawable(activity!!)))
                        .apply(RequestOptions().error(R.drawable.ic_image_white_grey_24dp))
                        .into(placeThirdImageView)

                placeNameTextView.text = place.name
                placeLocationTextView.text = place.location.address
                placePhoneTextView.text = place.phoneNumber
                placeDistanceTextView.text = resources.getString(
                        R.string.place_card_view_distance,
                        Utils.getRoundedDistance(place.distance)
                )
            }
        }
    }
}
