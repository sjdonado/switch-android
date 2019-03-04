package com.example.juan.aswitch.fragments


import android.app.Dialog
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.*

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.Utils
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.juan.aswitch.helpers.FragmentHandler
import com.example.juan.aswitch.helpers.Stories
import com.example.juan.aswitch.lib.SwipeCard
import com.example.juan.aswitch.models.Place
import com.example.juan.aswitch.models.Story
import com.example.juan.aswitch.models.User
import com.example.juan.aswitch.services.PlaceService
import com.example.juan.aswitch.services.UsersPlaceService
import com.mindorks.placeholderview.SwipeDecor
import kotlinx.android.synthetic.main.fragment_swipe.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class SwipeFragment : androidx.fragment.app.Fragment(), SwipeCard.Callback {

    private var places: ArrayList<Place> = ArrayList()
    private lateinit var place: Place
    private lateinit var placeService: PlaceService
    private lateinit var usersPlaceService: UsersPlaceService
    private lateinit var fragmentHandler: FragmentHandler
    private lateinit var progressDialog: Dialog
    private val animationDuration = 300
    private var isToUndo = false
    private var accept = false

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
            placeService.search(user.radius!!, user.categories!!, user.filters!!) { res ->
                val placesObjects = res.getJSONArray("data")
                Log.d("PLACES", res.toString())
                activity!!.runOnUiThread {
                    if(placesObjects.length() == 0) {
                        swipeNotFoundTextView.visibility = View.VISIBLE
                    } else {
                        createPlaces(placesObjects)
                        Log.d("PLACES_CALLBACK", places.toString())
                        places.reverse()
                        place = places[places.size - 1]
                        verifyPlaceStories()
                        updatePlaceViews(cardViewHolderSize)
                    }
                }
            }
        } else {
            updatePlaceViews(cardViewHolderSize)
        }

        swipeRejectButton.setOnClickListener {
            swipeRejectButton.isEnabled = false
            accept = false
            swipeView!!.doSwipe(accept)
        }

        swipeAcceptButton.setOnClickListener{
            swipeAcceptButton.isEnabled = false
            accept = true
            swipeView!!.doSwipe(accept)
        }

        swipeStoriesButton.setOnClickListener {
            swipeStoriesButton.isEnabled = false
//            Stories.get(place.downloadedStoriesIndex!!)
            Utils.openStories(activity!!, place.downloadedStoriesIndex!!)
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
            } else {
                place = places[it - 1]
                verifyPlaceStories()
                swipeRejectButton.isEnabled = true
                swipeAcceptButton.isEnabled = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        swipeStoriesButton.isEnabled = true
        if(::place.isInitialized) verifyPlaceStories()
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

    override fun onSwipeAccept(place: Place) {
        accept = true
    }

    override fun onCoverClick(place: Place) {
        fragmentHandler.add(PlaceDetailsFragment.getInstance(place, false))
    }

    override fun onSwipeUp() {
        isToUndo = true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun createPlaces(placesObjects: JSONArray) {
        for (i in 1..placesObjects.length()) {
            val place = Utils.parseJSONPlace(
                    placesObjects.getJSONObject(placesObjects.length() - i)
            )
            Log.d("PLACES_i_out", i.toString())
            if (!place.stories.isNullOrEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    place.downloadedStoriesIndex = Utils.downloadStories(place)
                    if (i == 1) verifyPlaceStories()
                }
            }
            places.add(place)
        }
    }

    private fun verifyPlaceStories() {
        Log.d("verifyPlaceStories", place.toString())
        if (place.downloadedStoriesIndex != null) swipeStoriesButton.show() else swipeStoriesButton.hide()
    }

}
