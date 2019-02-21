package com.example.juan.aswitch.fragments


import android.app.Activity
import android.content.Context
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
import com.example.juan.aswitch.services.UserService
import kotlinx.android.synthetic.main.fragment_edit_place.*
import kotlinx.android.synthetic.main.fragment_users.*
import org.json.JSONObject


class EditPlaceFragment : BaseFragment() {

    override fun getTitle(): String {
        return "Edit Place"
    }

    private lateinit var placeService: PlaceService
    private lateinit var userService: UserService
    private lateinit var place: Place
    private var editCoverImage: Boolean = false
    private var position: Int = 0
    private val emptyImageObject = ImageObject(null, null)

    companion object {
        fun getInstance(place: Place) = EditPlaceFragment().apply {
            arguments = Bundle().apply {
                putParcelable("PLACE", place)
            }
        }
        private const val PICK_IMAGE = 0
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
        return inflater.inflate(R.layout.fragment_edit_place, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeService = PlaceService(activity!!)
        userService = UserService(activity!!)

        loadImageInView(place.profilePicture.url, editPlaceCoverImageView)

        loadImageInView(place.images[0].url, editPlaceFirstImageView)
        loadImageInView(place.images[1].url, editPlaceSecondImageView)
        loadImageInView(place.images[2].url, editPlaceThirdImageView)

        if(!place.images[0].url.isNullOrBlank()) editPlaceRemoveFirstButton.visibility = View.VISIBLE
        if(!place.images[0].url.isNullOrBlank()) editPlaceRemoveFirstButton.visibility = View.VISIBLE
        if(!place.images[1].url.isNullOrBlank()) editPlaceRemoveSecondButton.visibility = View.VISIBLE
        if(!place.images[2].url.isNullOrBlank()) editPlaceRemoveThirdButton.visibility = View.VISIBLE

        editPlaceCoverImageView.setOnClickListener {
            editCoverImage = true
            Utils.openImagePickerIntent(this, PICK_IMAGE)
        }

        editPlaceFirstImageView.setOnClickListener {
            position = 0
            Utils.openImagePickerIntent(this, PICK_IMAGE)
        }

        editPlaceSecondImageView.setOnClickListener {
            position = 1
            Utils.openImagePickerIntent(this, PICK_IMAGE)
        }

        editPlaceThirdImageView.setOnClickListener {
            position = 2
            Utils.openImagePickerIntent(this, PICK_IMAGE)
        }

        editPlaceRemoveFirstButton.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("position", 0)
            placeService.removeImage(jsonObject) {
                activity!!.runOnUiThread {
                    place.images[0] = emptyImageObject
                    loadImageInView(null, editPlaceFirstImageView)
                    editPlaceRemoveFirstButton.visibility = View.INVISIBLE
                }
            }
        }

        editPlaceRemoveSecondButton.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("position", 1)
            placeService.removeImage(jsonObject) {
                activity!!.runOnUiThread {
                    place.images[1] = emptyImageObject
                    loadImageInView(null, editPlaceSecondImageView)
                    editPlaceRemoveSecondButton.visibility = View.INVISIBLE
                }
            }
        }

        editPlaceRemoveThirdButton.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("position", 2)
            placeService.removeImage(jsonObject) {
                activity!!.runOnUiThread {
                    place.images[2] = emptyImageObject
                    loadImageInView(null, editPlaceThirdImageView)
                    editPlaceRemoveThirdButton.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            PICK_IMAGE -> {
                if(resultCode == Activity.RESULT_OK && data != null) {
                    val image = Utils.copyInputStreamToFile(
                            activity!!,
                            activity!!.contentResolver!!.openInputStream(data.data!!)!!,
                            Utils.getMimeType(activity!!, data.data!!)!!
                    )
                    if (editCoverImage) {
                        editCoverImage = false
                        userService.uploadImage("profilePicture", image) { res ->
                            activity!!.runOnUiThread {
                                Utils.updateSharedPreferencesObjectValue(
                                        activity!!,
                                        "USER_OBJECT",
                                        res.getJSONObject("data")
                                )
                                val profilePicture = res.getJSONObject("data")
                                        .getJSONObject("profilePicture")
                                place.profilePicture = ImageObject(
                                        profilePicture.getString("ref"),
                                        profilePicture.getString("url")
                                )
                                loadImageInView(place.profilePicture.url, editPlaceCoverImageView)
                            }
                        }
                    } else {
                        placeService.uploadImage(position.toString(), image) { res ->
                            val newPlace = Utils.parseJSONPlace(res.getJSONObject("data"))
                            activity!!.runOnUiThread {
                                when(position) {
                                    0 -> {
                                        place.images[0] = newPlace.images[0]
                                        loadImageInView(place.images[0].url, editPlaceFirstImageView)
                                        editPlaceRemoveFirstButton.visibility = View.VISIBLE
                                    }
                                    1 -> {
                                        place.images[1] = newPlace.images[1]
                                        loadImageInView(place.images[1].url, editPlaceSecondImageView)
                                        editPlaceRemoveSecondButton.visibility = View.VISIBLE

                                    }
                                    2 -> {
                                        place.images[2] = newPlace.images[2]
                                        loadImageInView(place.images[2].url, editPlaceThirdImageView)
                                        editPlaceRemoveThirdButton.visibility = View.VISIBLE
                                    }
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