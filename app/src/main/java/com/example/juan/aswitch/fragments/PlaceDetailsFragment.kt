package com.example.juan.aswitch.fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.Utils
import kotlinx.android.synthetic.main.fragment_place_details.*
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import com.bumptech.glide.request.RequestOptions
import com.example.juan.aswitch.models.ImageObject
import com.example.juan.aswitch.models.Place
import com.example.juan.aswitch.models.User
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.SliderTypes.BaseSliderView
import com.glide.slider.library.SliderTypes.TextSliderView
import com.glide.slider.library.Tricks.ViewPagerEx
import kotlinx.android.synthetic.main.preference.*


class PlaceDetailsFragment : BaseFragment(),
        BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener  {

    override fun getTitle(): String {
        return "Place details"
    }

    private var qualify: Boolean = false
    private val images: ArrayList<ImageObject> = ArrayList()
    lateinit var place: Place
    lateinit var placeDetailsImageSlider: SliderLayout

    companion object {
        fun getInstance(place: Place, qualify: Boolean) = PlaceDetailsFragment().apply {
            arguments = Bundle().apply {
                putParcelable("PLACE", place)
                putBoolean("QUALIFY", qualify)
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getParcelable<Place>("PLACE")?.let {
            place = it
        }
        arguments?.getBoolean("QUALIFY")?.let {
            qualify = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_place_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val size = Utils.getGlideSize(activity!!) - 100
        placeDetailsImageSlider = activity!!.findViewById(R.id.placeDetailsImageSlider)

        val params = placeDetailsImageSlider.layoutParams
        params.height = size
        placeDetailsImageSlider.requestLayout()

        if (images.size == 0) {
            images.add(0, ImageObject(null, place.profilePicture.url))
            place.images.forEach {
                if(it.url != null) images.add(it)
            }
        }

        images.forEach {
            val sliderView = TextSliderView(context)
            sliderView
                    .image(it.url)
                    .setRequestOption(RequestOptions().centerCrop())
                    .setBackgroundColor(Color.WHITE)
                    .setProgressBarVisible(true)
                    .setOnSliderClickListener(this)
            placeDetailsImageSlider.addSlider(sliderView)
        }

        placeDetailsImageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        placeDetailsImageSlider.addOnPageChangeListener(this)

        placeDetailsNameTextView.text = place.name
        placeDetailsLocationTextView.text = place.location.address
        placeDetailsPhoneTextView.text = place.phoneNumber
        placeDetailsDistanceTextView.text = resources.getString(
                R.string.place_details_distance,
                Utils.getRoundedDistance(place.distance)
        )
        placeDetailsDescriptionTextView.text = place.description
        placeDetailsRatingBar.rating = place.rate!!.qualify!!.toFloat()
        placeDetailsRatingTextView.text = place.rate!!.qualify!!.toString()
        placeDetailsRatingSizeTextView.text = resources.getString(
                R.string.place_details_rate_size,
                place.rate!!.size!!.toString()
        )
        placeDetailsTimeTextView.text = resources.getString(
                R.string.place_details_time,
                place.openingTime!!.hourOfDay.toString(),
                place.openingTime!!.minute.toString(),
                place.closingTime!!.hourOfDay.toString(),
                place.closingTime!!.minute.toString()
        )
        placeDetailsTimeChip.text = Utils.setChipTime(activity!!, place.openingTime!!, place.closingTime!!)

        if (qualify) {
            placeDetailsQualifyButton.show()
            placeDetailsQualifyButton.setOnClickListener {
                val ft = fragmentManager!!.beginTransaction()
                val prev = fragmentManager!!.findFragmentByTag("dialog")
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)
                val dialogFragment = QualifyFragment.getInstance(place)
                dialogFragment.show(ft, "dialog")
            }
        }

        placeDetailsGoButton.setOnClickListener {
            placeDetailsGoButton.isEnabled = false
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
            placeDetailsCallButton.isEnabled = false
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${place.phoneNumber}")
            }
            if (intent.resolveActivity(activity!!.packageManager) != null) {
                startActivity(intent)
            }
        }

        Log.d("PLACE_OBJECT", "geo:${place.location.lat},${place.location.lng}")
    }

    override fun onResume() {
        super.onResume()
        placeDetailsGoButton.isEnabled = true
        placeDetailsCallButton.isEnabled = true
    }

    override fun onSliderClick(p0: BaseSliderView?) {}

    override fun onPageScrollStateChanged(p0: Int) {}

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

    override fun onPageSelected(p0: Int) {}

}
