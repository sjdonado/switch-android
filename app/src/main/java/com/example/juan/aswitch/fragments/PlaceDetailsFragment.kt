package com.example.juan.aswitch.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.Utils
import org.json.JSONObject
import kotlinx.android.synthetic.main.fragment_place_details.*

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

        nameDetailsTextView.text = placeObject.getString("name")

        Log.d("PLACE_OBJECT", placeObject.getString("name"))
    }
}
