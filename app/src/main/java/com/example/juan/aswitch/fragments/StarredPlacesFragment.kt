package com.example.juan.aswitch.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.juan.aswitch.R
import com.example.juan.aswitch.adapters.PlacesAdapter
import com.example.juan.aswitch.helpers.FragmentHandler
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.models.Place
import com.example.juan.aswitch.services.PlaceService
import kotlinx.android.synthetic.main.fragment_starred_places.*
import com.example.juan.aswitch.lib.SwipeToDeleteCallback
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.juan.aswitch.adapters.PlacesSwipeAdapter
import com.example.juan.aswitch.services.UsersPlaceService


class StarredPlacesFragment : androidx.fragment.app.Fragment() {

    private var places: ArrayList<Place> = ArrayList()
    private lateinit var placeService: PlaceService
    private lateinit var placeUsersPlaceService: UsersPlaceService

    private lateinit var fragmentHandler: FragmentHandler

    companion object {
        fun getInstance(): StarredPlacesFragment = StarredPlacesFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_starred_places, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeService = PlaceService(activity!!)
        placeUsersPlaceService= UsersPlaceService(activity!!)

        fragmentHandler = FragmentHandler(activity!! as AppCompatActivity, R.id.menu_fragment_container)

        val viewManager = LinearLayoutManager(activity)
        val placesAdapter = PlacesAdapter(view, activity!!, places,
            object: PlacesAdapter.OnClickListener {
                override fun onClick(place: Place) {
                    fragmentHandler.add(PlaceDetailsFragment.getInstance(place, true))
                }
            },
            object: PlacesSwipeAdapter.OnSwipeListener {
                override fun onDelete(place: Place) {
                    Log.d("DELETED", place.toString())
                    placeUsersPlaceService.remove(place.id) { _, _ -> }
                }
            }
        )

        if(places.size == 0) {
            placeService.starredPlaces { err, res ->
                if (!err) {
                    val placesObjects = res.getJSONArray("data")
                    Log.d("STARRED_PLACES", res.toString())
                    if(placesObjects.length() == 0 ) {
                        activity!!.runOnUiThread {
                            starredNotFoundTextView.visibility = View.VISIBLE
                        }
                    } else {
                        for (i in 1..placesObjects.length()) {
                            val place = Utils.parseJSONPlace(placesObjects.getJSONObject(placesObjects.length() - i))
                            places.add(place)
                        }
                        activity!!.runOnUiThread {
                            placesAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        starredPlacesRecyclerView.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = placesAdapter
        }

        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(context!!, placesAdapter))
        itemTouchHelper.attachToRecyclerView(starredPlacesRecyclerView)
    }
}
