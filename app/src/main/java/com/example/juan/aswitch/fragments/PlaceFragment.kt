package com.example.juan.aswitch.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.models.ImageObject
import com.example.juan.aswitch.models.Place
import com.example.juan.aswitch.services.PlaceService
import kotlinx.android.synthetic.main.fragment_place.*
import org.json.JSONObject


class PlaceFragment : androidx.fragment.app.Fragment() {

    private lateinit var placeService: PlaceService
    private lateinit var place: Place
    private var position: Int = 0

    companion object {
        fun getInstance(): PlaceFragment = PlaceFragment()
        private const val PICK_IMAGE = 0
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
            Log.d("PLACE_OBJECT_DATA", it.getJSONObject("data").toString())
            Log.d("PLACE_OBJECT", place.toString())
            activity!!.runOnUiThread {

                loadImageInView(place.profilePicture.url, placeCoverImageView)

                loadImageInView(place.images[0].url, placeFirstImageView)
                loadImageInView(place.images[1].url, placeSecondImageView)
                loadImageInView(place.images[2].url, placeThirdImageView)

                if(!place.images[0].url.isNullOrBlank()) placeRemoveFirstButton.visibility = View.VISIBLE
                if(!place.images[1].url.isNullOrBlank()) placeRemoveSecondButton.visibility = View.VISIBLE
                if(!place.images[2].url.isNullOrBlank()) placeRemoveThirdButton.visibility = View.VISIBLE

                placeNameTextView.text = place.name
                placeLocationTextView.text = place.location.address
                placePhoneTextView.text = place.phoneNumber
                placeDistanceTextView.text = resources.getString(
                        R.string.place_card_view_distance,
                        Utils.getRoundedDistance(place.distance)
                )
            }
        }

        placeFirstImageView.setOnClickListener {
            position = 0
            Utils.openImagePickerIntent(this, PICK_IMAGE)
        }

        placeSecondImageView.setOnClickListener {
            position = 1
            Utils.openImagePickerIntent(this, PICK_IMAGE)
        }

        placeThirdImageView.setOnClickListener {
            position = 2
            Utils.openImagePickerIntent(this, PICK_IMAGE)
        }

        placeRemoveFirstButton.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("position", 0)
            placeService.removeImage(jsonObject) {
                activity!!.runOnUiThread {
                    loadImageInView(null, placeFirstImageView)
                    placeRemoveFirstButton.visibility = View.INVISIBLE
                }
            }
        }

        placeRemoveSecondButton.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("position", 1)
            placeService.removeImage(jsonObject) {
                activity!!.runOnUiThread {
                    loadImageInView(null, placeSecondImageView)
                    placeRemoveSecondButton.visibility = View.INVISIBLE
                }
            }
        }

        placeRemoveThirdButton.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("position", 2)
            placeService.removeImage(jsonObject) {
                activity!!.runOnUiThread {
                    loadImageInView(null, placeThirdImageView)
                    placeRemoveThirdButton.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            PICK_IMAGE -> {
                if(resultCode == Activity.RESULT_OK && data != null) {
                    Log.i("DATA_URI", data.data!!.path)
                    val image = Utils.copyInputStreamToFile(
                            activity!!,
                            activity!!.contentResolver!!.openInputStream(data.data!!)!!,
                            Utils.getMimeType(activity!!, data.data!!)!!
                    )
                    placeService.uploadImage(position.toString(), image) { res ->
                        place = Utils.parseJSONPlace(res.getJSONObject("data"))
                        activity!!.runOnUiThread {
                            when(position) {
                                0 -> {
                                    loadImageInView(place.images[0].url, placeFirstImageView)
                                    placeRemoveFirstButton.visibility = View.VISIBLE
                                }
                                1 -> {
                                    loadImageInView(place.images[1].url, placeSecondImageView)
                                    placeRemoveSecondButton.visibility = View.VISIBLE

                                }
                                2 -> {
                                    loadImageInView(place.images[2].url, placeThirdImageView)
                                    placeRemoveThirdButton.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                Log.e("INTENT", "Unrecognized request code")
            }
        }
    }

    private fun loadImageInView(url: String?, imageView: ImageView) {
        Glide.with(activity!!).load(url)
                .apply(RequestOptions().placeholder(Utils.getCircularProgressDrawable(activity!!)))
                .apply(RequestOptions().error(R.drawable.ic_image_white_grey_24dp))
                .into(imageView)
    }
}
