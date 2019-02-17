package com.example.juan.aswitch.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.FragmentHandler
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.models.Place
import com.example.juan.aswitch.services.PlaceService


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

        fragmentHandler = FragmentHandler(activity!! as AppCompatActivity, R.id.stories_fragment_container)

        placeService.get {
            place = Utils.parseJSONPlace(it.getJSONObject("data"))
            activity!!.runOnUiThread {
                fragmentHandler.add(EditPlaceFragment.getInstance(place, "stories"))
            }
        }

    }

}
