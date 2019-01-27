package com.example.juan.aswitch.fragments


import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.*

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.Utils
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.juan.aswitch.helpers.FragmentHandler
import com.example.juan.aswitch.lib.SwipeCard
import com.example.juan.aswitch.models.Place
import com.example.juan.aswitch.models.User
import com.example.juan.aswitch.services.PlaceService
import com.example.juan.aswitch.services.UsersPlaceService
import com.google.gson.JsonObject
import com.mindorks.placeholderview.SwipeDecor
import kotlinx.android.synthetic.main.fragment_swipe.*
import org.json.JSONObject


class SwipeFragment : androidx.fragment.app.Fragment(), SwipeCard.Callback {

    private var places: ArrayList<Place> = ArrayList()
    private lateinit var placeService: PlaceService
    private lateinit var usersPlaceService: UsersPlaceService
    private val animationDuration = 300
    private var isToUndo = false
    private var accept = false
    private lateinit var user: User
    private lateinit var fragmentHandler: FragmentHandler

    companion object {
        fun getInstance(): SwipeFragment = SwipeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_swipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentHandler = FragmentHandler(activity as AppCompatActivity, R.id.menu_fragment_container)

        placeService = PlaceService(activity!!)
        usersPlaceService = UsersPlaceService(activity!!)

        val user = Utils.getSharedPreferencesUserObject(activity!!)

        val bottomMargin = Utils.dpToPx(160)
        val windowSize = Utils.getDisplaySize(activity!!.windowManager)
        swipeView!!.builder
                .setDisplayViewCount(3)
                .setIsUndoEnabled(true)
                .setSwipeVerticalThreshold(Utils.dpToPx(50))
                .setSwipeHorizontalThreshold(Utils.dpToPx(50))
                .setHeightSwipeDistFactor(10f)
                .setWidthSwipeDistFactor(5f)
                .setSwipeDecor(SwipeDecor()
                        .setViewWidth(windowSize.x)
                        .setViewHeight(windowSize.y - bottomMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(20)
                        .setSwipeAnimTime(animationDuration)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.swipe_in)
                        .setSwipeOutMsgLayoutId(R.layout.swipe_out))


        val cardViewHolderSize = Point(windowSize.x, windowSize.y - bottomMargin)

        if(places.size == 0) {
//            if(userObject.has("filters")) Log.d("USER_OBJECT_FILTERS", userObject.getJSONArray("filters").toString())
            placeService.search(user.radius!!, user.filters!!) {res ->
                val placesObjects = res.getJSONArray("data")
                Log.d("PLACES", res.toString())
                if(placesObjects.length() == 0) {
                    activity!!.runOnUiThread {
                        swipeNotFoundTextView.visibility = View.VISIBLE
                    }
                } else {
                    for (i in 0..(placesObjects.length()-1)) {
                        val place = Utils.parseJSONPlace(
                            placesObjects.getJSONObject(i)
                        )
                        places.add(place)
                    }
                    activity!!.runOnUiThread {
                        places.reverse()
                        updatePlaceViews(cardViewHolderSize)
                    }
                }
            }
        } else {
            updatePlaceViews(cardViewHolderSize)
        }

        swipeRejectButton.setOnClickListener {
            accept = false
            swipeView!!.doSwipe(accept)
        }

        swipeAcceptButton.setOnClickListener{
            accept = true
            swipeView!!.doSwipe(accept)
        }
//
//        undoBtn.setOnClickListener({ swipeView!!.undoLastSwipe() })

        swipeView!!.addItemRemoveListener {
            if (isToUndo) swipeView!!.undoLastSwipe()
            if(accept) {
                usersPlaceService.accept(user.id!!, places[it].id) {}
                accept = false
            } else {
                usersPlaceService.reject(user.id!!, places[it].id) {}
            }
            if(isToUndo) {
                isToUndo = false
            } else {
                places.remove(places[it])
            }
            if(it == 0) {
                swipeNotFoundTextView.visibility = View.VISIBLE
                swipeRejectButton.hide()
                swipeAcceptButton.hide()
            }
            Log.d("ITEM_REMOVE_LISTENER", places.size.toString())
            Log.d("ITEM_REMOVE_LISTENER", it.toString())
        }
    }

    private fun updatePlaceViews(cardViewHolderSize: Point) {
        for (i in 1..places.size) {
            swipeView!!.addView(
                SwipeCard(activity!!, places[places.size - i], cardViewHolderSize, this)
            )
        }
        swipeRejectButton.show()
        swipeAcceptButton.show()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.filterAction -> {
                fragmentHandler.add(FiltersFragment.getInstance())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onSwipeAccept(place: Place) {
        accept = true
    }

    override fun onCoverClick(place: Place) {
        fragmentHandler.add(PlaceDetailsFragment.getInstance(place, false))
    }

    override fun onSwipeUp() {
        isToUndo = true
    }
}
