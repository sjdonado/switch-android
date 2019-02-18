package com.example.juan.aswitch.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.juan.aswitch.R
import com.example.juan.aswitch.activities.StoriesActivity
import com.example.juan.aswitch.helpers.FragmentHandler
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.models.Place
import com.example.juan.aswitch.services.PlaceService
import kotlinx.android.synthetic.main.fragment_edit_stories.*
import android.util.Log
import com.example.juan.aswitch.models.ImageObject
import com.example.juan.aswitch.models.Story


class EditStoriesFragment : androidx.fragment.app.Fragment() {

    private lateinit var placeService: PlaceService
    private lateinit var place: Place
    private lateinit var fragmentHandler: FragmentHandler

    companion object {
        fun getInstance(): EditStoriesFragment = EditStoriesFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_stories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeService = PlaceService(activity!!)

        fragmentHandler = FragmentHandler(activity!! as AppCompatActivity, R.id.storiesContainer)

        placeService.get {
            place = Utils.parseJSONPlace(it.getJSONObject("data"))
            activity!!.runOnUiThread {
                fragmentHandler.simpleAdd(EditPlaceFragment.getInstance(place, "stories"))
            }
        }

        storiesWatchButton.setOnClickListener {
            Utils.openStories(activity!!, place.stories)
//            storiesArray.clear()
//            val notNullStoriesArray = place.stories.mapNotNull { t: ImageObject ->
//                if (t.ref !== null) t else null
//            }
//            notNullStoriesArray.forEachIndexed { index, obj ->
//                Utils.bitmapFromUrl(activity!!, storiesArray, index, notNullStoriesArray.size, obj.url!!) {
//                    val storiesIntent = Intent(activity, StoriesActivity::class.java)
//                    storiesIntent.putExtra("stories", storiesArray)
//                    startActivity(storiesIntent)
//                }
//            }
        }

    }
}
