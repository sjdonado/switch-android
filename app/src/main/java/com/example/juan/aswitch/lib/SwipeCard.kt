package com.example.juan.aswitch.lib

import com.example.juan.aswitch.R
import android.content.Context
import android.graphics.Point
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.juan.aswitch.fragments.HomeFragment
import com.example.juan.aswitch.fragments.PlaceDetailsFragment
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.models.Place
import com.mindorks.placeholderview.SwipeDirection
import com.mindorks.placeholderview.annotations.*
import com.mindorks.placeholderview.annotations.swipe.*
import kotlin.math.sqrt

@Layout(R.layout.place_card_view)
class SwipeCard(private val context: Context,
                private val place: Place,
                private val cardViewHolderSize: Point,
                private val callback: Callback) {

    @View(R.id.placeCardImageView)
    lateinit var placeCardImageView: ImageView

    @View(R.id.nameCardTextView)
    lateinit var nameCardTextView: TextView

    @View(R.id.locationCardTextView)
    lateinit var locationCardTextView: TextView

    @SwipeView
    lateinit var swipeView: android.view.View

    @JvmField
    @Position
    var position: Int = 0

    @Resolve
    fun onResolved() {
        Glide.with(context).load(place.url)
                .into(placeCardImageView)
        nameCardTextView.text = "${place.name},  ${place.age}"
        locationCardTextView.text = place.location
        swipeView.alpha = 1f
    }

    @Click(R.id.placeCardImageView)
    fun onClick() {
        Log.d("EVENT", "profileImageView click")
    }

    @SwipeOutDirectional
    fun onSwipeOutDirectional(direction: SwipeDirection) {
        Log.d("DEBUG", "SwipeOutDirectional " + direction.name)
        if (direction.direction == SwipeDirection.TOP.direction) {
            callback.onSwipeUp()
        }
    }

    @SwipeCancelState
    fun onSwipeCancelState() {
        Log.d("DEBUG", "onSwipeCancelState")
        swipeView.alpha = 1f
    }

    @SwipeInDirectional
    fun onSwipeInDirectional(direction: SwipeDirection) {
        Log.d("DEBUG", "SwipeInDirectional " + direction.name)
    }

    @SwipingDirection
    fun onSwipingDirection(direction: SwipeDirection) {
        if(direction.name == "RIGHT") {
            Utils.openFragment(
                context as AppCompatActivity,
                R.id.menu_fragment_container,
                PlaceDetailsFragment.getInstance()
            )
        }
        Log.d("DEBUG", "SwipingDirection " + direction.name)
    }

    @SwipeTouch
    fun onSwipeTouch(xStart: Float, yStart: Float, xCurrent: Float, yCurrent: Float) {

        val cardHolderDiagonalLength =
                sqrt(Math.pow(cardViewHolderSize.x.toDouble(), 2.0)
                        + (Math.pow(cardViewHolderSize.y.toDouble(), 2.0)))
        val distance = sqrt(Math.pow(xCurrent.toDouble() - xStart.toDouble(), 2.0)
                + (Math.pow(yCurrent.toDouble() - yStart, 2.0)))

        val alpha = 1 - distance / cardHolderDiagonalLength

        Log.d("DEBUG", "onSwipeTouch "
                + " xStart : " + xStart
                + " yStart : " + yStart
                + " xCurrent : " + xCurrent
                + " yCurrent : " + yCurrent
                + " distance : " + distance
                + " TotalLength : " + cardHolderDiagonalLength
                + " alpha : " + alpha
        )

        swipeView.alpha = alpha.toFloat()
    }

    interface Callback {
        fun onSwipeUp()
    }
}