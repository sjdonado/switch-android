package com.example.juan.aswitch.fragments


import android.os.Bundle
import android.util.Log
import android.view.*

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.Functions
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.juan.aswitch.adapters.Place
import com.example.juan.aswitch.adapters.PlacesAdapter
import com.example.juan.aswitch.services.PlaceService
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : androidx.fragment.app.Fragment() {

    private var places: ArrayList<Place> = ArrayList()
    private lateinit var placeService: PlaceService

    companion object {
        fun getInstance(): HomeFragment = HomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        placeService = PlaceService(activity!!)

        val viewManager = LinearLayoutManager(activity)
        val viewAdapter = PlacesAdapter(places,
            object: PlacesAdapter.OnClickListener {
                override fun onClick(place: Place) {
                    Log.d("CLICK", place.toString())
                }

            }
        )

        placeService.search {res ->
            val placesObjects = res.getJSONArray("data")
            Log.d("PLACES", res.toString())
            for (i in 0..(placesObjects.length() - 1)) {
                val item = placesObjects.getJSONObject(i)
                places.add(Place(item.getString("uid")))
            }
            activity!!.runOnUiThread {
                viewAdapter.notifyDataSetChanged()
            }
        }

        val recyclerView = home_recycler_view.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }
        
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.filter_action -> {
                Functions.openFragment(activity as AppCompatActivity, R.id.menu_fragment_container, FiltersFragment.getInstance())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
