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
import android.graphics.Color
import android.net.Uri
import com.bumptech.glide.request.RequestOptions
import com.example.juan.aswitch.models.ImageObject
import com.example.juan.aswitch.models.Place
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.SliderTypes.BaseSliderView
import com.glide.slider.library.SliderTypes.TextSliderView
import com.glide.slider.library.Tricks.ViewPagerEx


class PlaceDetailsFragment : androidx.fragment.app.Fragment(),
        BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener  {

    lateinit var place: Place
    lateinit var placeDetailsImageSlider: SliderLayout

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

        placeDetailsImageSlider = activity!!.findViewById(R.id.placeDetailsImageSlider)
        placeDetailsImageSlider.removeAllSliders()

        val params = placeDetailsImageSlider.layoutParams
        params.height = size
        placeDetailsImageSlider.requestLayout()

        place.images.add(0, ImageObject(null, place.profilePicture.url))

        place.images.forEach {
            if(it.url != null) {
                val sliderView = TextSliderView(context)
                sliderView
                        .image(it.url)
                        .setRequestOption(RequestOptions().centerCrop())
                        .setBackgroundColor(Color.WHITE)
                        .setProgressBarVisible(true)
                        .setOnSliderClickListener(this)
                placeDetailsImageSlider.addSlider(sliderView)
            }
        }

        placeDetailsImageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        placeDetailsImageSlider.addOnPageChangeListener(this)

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

    override fun onSliderClick(p0: BaseSliderView?) {
    }

    override fun onPageScrollStateChanged(p0: Int) {
    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
    }

    override fun onPageSelected(p0: Int) {
    }
}
