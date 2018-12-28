package com.example.juan.aswitch.fragments


import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.*

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.Utils
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.juan.aswitch.lib.Card
import com.example.juan.aswitch.models.Place
import com.example.juan.aswitch.services.PlaceService
import com.mindorks.placeholderview.SwipeDecor
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONObject


class HomeFragment : androidx.fragment.app.Fragment(), Card.Callback {

    private var places: ArrayList<Place> = ArrayList()
    private lateinit var placeService: PlaceService
    private val animationDuration = 300
    private var isToUndo = false

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

        placeService.search {res ->
            val placesObjects = res.getJSONArray("data")
            Log.d("PLACES", res.toString())
            for (i in 0..(placesObjects.length() - 1)) {
                val item = placesObjects.getJSONObject(i)
                val image = item.getJSONObject("profilePicture")
                val place = Place(item.getString("uid"), image.getString("url"), item.getInt("radius"), "test")
                places.add(place)
                activity!!.runOnUiThread {
                    swipeView!!.addView(Card(activity!!, place, cardViewHolderSize, this))
                }
            }
        }

//        rejectBtn.setOnClickListener({ swipeView!!.doSwipe(false) })
//
//        acceptBtn.setOnClickListener({ swipeView!!.doSwipe(true) })
//
//        undoBtn.setOnClickListener({ swipeView!!.undoLastSwipe() })

        swipeView!!.addItemRemoveListener {
            if (isToUndo) {
                isToUndo = false
                swipeView!!.undoLastSwipe()
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.filter_action -> {
                Utils.openFragment(activity as AppCompatActivity, R.id.menu_fragment_container, FiltersFragment.getInstance())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onSwipeUp() {
        Utils.showSnackbar(view!!, "SUPER LIKE! Show any dialog here.")
        isToUndo = true
    }
}
