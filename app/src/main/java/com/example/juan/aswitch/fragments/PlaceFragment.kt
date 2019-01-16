package com.example.juan.aswitch.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

import com.example.juan.aswitch.R
import com.example.juan.aswitch.activities.BaseActivity
import com.example.juan.aswitch.helpers.FragmentHandler
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.models.Place
import com.example.juan.aswitch.services.PlaceService
import kotlinx.android.synthetic.main.fragment_place.*


class PlaceFragment : androidx.fragment.app.Fragment() {

    private lateinit var placeService: PlaceService
    private lateinit var place: Place
    private var editMode: Boolean = false
    private lateinit var fragmentHandler: FragmentHandler

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

        fragmentHandler = FragmentHandler(activity!! as AppCompatActivity, R.id.place_fragment_container)
        BaseActivity.BACK_STACK_MIN_ENTRY_COUNT = 2

        placeService.get {
            place = Utils.parseJSONPlace(it.getJSONObject("data"))
            activity!!.runOnUiThread {
                fragmentHandler.add(PlaceDetailsFragment.getInstance(place, false))
            }
        }

        placeEditOrSaveButton.setOnClickListener {
            editMode = !editMode
            if(editMode) {
                placeEditOrSaveButton.setImageResource(R.drawable.ic_save_white_24dp)
                fragmentHandler.add(EditPlaceFragment.getInstance(place))
            } else {
                placeEditOrSaveButton.setImageResource(R.drawable.ic_edit_white_24dp)
                fragmentHandler.add(PlaceDetailsFragment.getInstance(place, false))
            }
        }
    }

}
