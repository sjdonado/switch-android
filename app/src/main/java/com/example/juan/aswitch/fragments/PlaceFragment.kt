package com.example.juan.aswitch.fragments


import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.FragmentHandler
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.models.Place
import com.example.juan.aswitch.services.PlaceService


class PlaceFragment : androidx.fragment.app.Fragment() {

    private lateinit var placeService: PlaceService
    private lateinit var place: Place
    private lateinit var fragmentHandler: FragmentHandler
    private lateinit var placeMenu: Menu

    private val backStackListener = FragmentManager.OnBackStackChangedListener {
        if(fragmentManager?.backStackEntryCount == 2)
            placeMenu.findItem(R.id.editPlaceAction).isVisible = true
    }

    companion object {
        fun getInstance(): PlaceFragment = PlaceFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
            activity!!.runOnUiThread {
                Utils.setSharedPreferencesPlaceObject(activity!!, it.getJSONObject("data"))
                PlaceDetailsFragment.TITLE = FragmentHandler.NO_ADD_TO_BACK_STACK
                fragmentHandler.add(PlaceDetailsFragment.getInstance(place, false))
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.editPlaceAction -> {
                item.isVisible = false
                fragmentHandler.add(EditPlaceFragment.getInstance(place))
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_place_menu, menu)
        placeMenu = menu
        fragmentHandler = FragmentHandler(activity!! as AppCompatActivity, R.id.place_fragment_container)
        activity!!.supportFragmentManager.addOnBackStackChangedListener(backStackListener)
        super.onCreateOptionsMenu(menu, inflater)
    }

}
