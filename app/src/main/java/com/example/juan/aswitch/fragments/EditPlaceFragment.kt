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
import java.io.File


class EditPlaceFragment : BaseFragment() {

    override fun getTitle(): String {
        return "Edit Place"
    }

    private lateinit var placeService: PlaceService
    private lateinit var place: Place
    private lateinit var path: String
    private var position: Int = 0
    private val emptyImageObject = ImageObject(null, null)

    companion object {
        fun getInstance(place: Place, path: String) = EditPlaceFragment().apply {
            arguments = Bundle().apply {
                putParcelable("PLACE", place)
                putString("PATH", path)
            }
        }
        private const val PICK_IMAGE = 0
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getParcelable<Place>("PLACE")?.let {
            place = it
        }
        arguments?.getString("PATH")?.let {
            path = it
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

//        when (path) {
//            "images" -> {
//                editPlaceTitleTextView.text = getString(R.string.edit_place_fragment_images_title)
//            }
//            "stories" -> {
//                editPlaceTitleTextView.text = getString(R.string.edit_place_fragment_stories_title)
//            }
//        }

        Log.d("PLACE", place.toString())

        loadImageInView(getMedia(place, 0).url, editPlaceFirstImageView)
        loadImageInView(getMedia(place, 1).url, editPlaceSecondImageView)
        loadImageInView(getMedia(place, 2).url, editPlaceThirdImageView)
        loadImageInView(getMedia(place, 3).url, editPlaceFourthImageView)

        if(!getMedia(place, 0).url.isNullOrBlank()) editPlaceRemoveFirstButton.visibility = View.VISIBLE
        if(!getMedia(place, 1).url.isNullOrBlank()) editPlaceRemoveSecondButton.visibility = View.VISIBLE
        if(!getMedia(place, 2).url.isNullOrBlank()) editPlaceRemoveThirdButton.visibility = View.VISIBLE
        if(!getMedia(place, 3).url.isNullOrBlank()) editPlaceRemoveFourthButton.visibility = View.VISIBLE

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

        editPlaceFourthImageView.setOnClickListener {
            position = 3
            Utils.openImagePickerIntent(this, PICK_IMAGE)
        }

        editPlaceRemoveFirstButton.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("position", 0)
            removeMedia(jsonObject) {
                activity!!.runOnUiThread {
                    updateMedia(place, 0, emptyImageObject)
                    loadImageInView(null, editPlaceFirstImageView)
                    editPlaceRemoveFirstButton.visibility = View.INVISIBLE
                }
            }
        }

        editPlaceRemoveSecondButton.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("position", 1)
            removeMedia(jsonObject) {
                activity!!.runOnUiThread {
                    updateMedia(place, 1, emptyImageObject)
                    loadImageInView(null, editPlaceSecondImageView)
                    editPlaceRemoveSecondButton.visibility = View.INVISIBLE
                }
            }
        }

        editPlaceRemoveThirdButton.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("position", 2)
            removeMedia(jsonObject) {
                activity!!.runOnUiThread {
                    updateMedia(place, 2, emptyImageObject)
                    loadImageInView(null, editPlaceThirdImageView)
                    editPlaceRemoveThirdButton.visibility = View.INVISIBLE
                }
            }
        }

        editPlaceRemoveFourthButton.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("position", 3)
            removeMedia(jsonObject) {
                activity!!.runOnUiThread {
                    updateMedia(place, 3, emptyImageObject)
                    loadImageInView(null, editPlaceFourthImageView)
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
                    uploadMedia(position.toString(), image) { res ->
                        val newPlace = Utils.parseJSONPlace(res.getJSONObject("data"))
                        activity!!.runOnUiThread {
                            when(position) {
                                0 -> {
                                    updateMedia(place, 0, getMedia(newPlace, 0))
                                    loadImageInView(getMedia(place, 0).url, editPlaceFirstImageView)
                                    editPlaceRemoveFirstButton.visibility = View.VISIBLE
                                }
                                1 -> {
                                    updateMedia(place, 1, getMedia(newPlace, 1))
                                    loadImageInView(getMedia(place, 1).url, editPlaceSecondImageView)
                                    editPlaceRemoveSecondButton.visibility = View.VISIBLE
                                }
                                2 -> {
                                    updateMedia(place, 2, getMedia(newPlace, 2))
                                    loadImageInView(getMedia(place, 2).url, editPlaceThirdImageView)
                                    editPlaceRemoveThirdButton.visibility = View.VISIBLE
                                }
                                3 -> {
                                    updateMedia(place, 3, getMedia(newPlace, 3))
                                    loadImageInView(getMedia(place, 3).url, editPlaceFourthImageView)
                                    editPlaceRemoveFourthButton.visibility = View.VISIBLE
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

    private fun getMedia(place: Place, position: Int): ImageObject {
        return when (path) {
            "images" -> place.images[position]
            "stories" -> place.stories[position]
            else -> { // Note the block
                ImageObject(null, null)
            }
        }
    }

    private fun updateMedia(place: Place, position: Int, imageObject: ImageObject) {
         when (path) {
            "images" -> {
                place.images[position] = imageObject
            }
            "stories" -> {
                place.stories[position] = imageObject
            }
         }
    }

    private fun uploadMedia(position: String, image: File, callback: (response: JSONObject) -> Unit) {
        when (path) {
            "images" -> {
                placeService.uploadImage(position, image, callback)
            }
            "stories" -> {
                placeService.uploadStory(position, image, callback)
            }
        }
    }

    private fun removeMedia(jsonObject: JSONObject, callback: (response: JSONObject) -> Unit) {
        when (path) {
            "images" -> {
                placeService.removeImage(jsonObject, callback)
            }
            "stories" -> {
                placeService.removeStory(jsonObject, callback)
            }
        }
    }
}
