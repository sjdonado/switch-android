package com.example.juan.aswitch.fragments


import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.*

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.Utils
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.juan.aswitch.lib.SwipeCard
import com.example.juan.aswitch.models.Place
import com.example.juan.aswitch.services.PlaceService
import com.example.juan.aswitch.services.UsersPlaceService
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
    private var userObject: JSONObject = JSONObject()

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
        placeService = PlaceService(activity!!)
        usersPlaceService = UsersPlaceService(activity!!)

        val userObjectValue = Utils.getSharedPreferencesStringValue(
                activity!!,
                "USER_OBJECT"
        )
        if(userObjectValue != null) userObject = JSONObject(userObjectValue)

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
            placeService.search(userObject.getInt("radius")) {res ->
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
            if (isToUndo) {
                isToUndo = false
                swipeView!!.undoLastSwipe()
            }
            if(accept) {
                usersPlaceService.accept(userObject.getString("id"), places[it].id) {}
                accept = false
            } else {
                usersPlaceService.reject(userObject.getString("id"), places[it].id) {}
            }
            if(it == 0) {
                swipeNotFoundTextView.visibility = View.VISIBLE
                swipeRejectButton.hide()
                swipeAcceptButton.hide()
            }
            places.remove(places[it])
            Log.d("ITEM_REMOVE_LISTENER", accept.toString())
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
            R.id.filter_action -> {
                Utils.openFragment(
                        activity as AppCompatActivity,
                        R.id.menu_fragment_container,
                        FiltersFragment.getInstance()
                )
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
        Utils.openFragment(
                activity as AppCompatActivity,
                R.id.menu_fragment_container,
                PlaceDetailsFragment.getInstance(place)
        )
    }

    override fun onSwipeUp() {
        Utils.showSnackbar(view!!, "SUPER LIKE! Show any dialog here.")
        isToUndo = true
    }
}
