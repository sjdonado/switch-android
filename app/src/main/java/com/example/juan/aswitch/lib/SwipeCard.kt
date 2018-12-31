package com.example.juan.aswitch.lib

import android.app.Activity
import com.example.juan.aswitch.R
import android.graphics.Point
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.models.Place
import com.mindorks.placeholderview.SwipeDirection
import com.mindorks.placeholderview.annotations.*
import com.mindorks.placeholderview.annotations.swipe.*
import kotlin.math.sqrt

@Layout(R.layout.place_card_view)
class SwipeCard(private val context: Activity,
                private val place: Place,
                private val cardViewHolderSize: Point,
                private val callback: Callback) {

    @View(R.id.placeCardCoverImageView)
    lateinit var placeCardCoverImageView: ImageView

    @View(R.id.placeCardNameTextView)
    lateinit var placeCardNameTextView: TextView

    @View(R.id.placeCardLocationTextView)
    lateinit var placeCardLocationTextView: TextView

    @View(R.id.placeCardDistanceTextView)
    lateinit var placeCardDistanceTextView: TextView

    @SwipeView
    lateinit var swipeView: android.view.View

    @JvmField
    @Position
    var position: Int = 0

    @Resolve
    fun onResolved() {
        val windowY = Utils.getDisplaySize(context.windowManager).y
        val size = windowY - Utils.dpToPx(windowY / 6)
        Log.d("CARD_SIZE", size.toString())
        Glide.with(context).load(place.imgUrl)
                .apply(RequestOptions().placeholder(Utils.getCircularProgressDrawable(context)))
                .apply(RequestOptions().override(size, size))
                .into(placeCardCoverImageView)
        placeCardNameTextView.text = place.name
        placeCardLocationTextView.text = place.address
        placeCardDistanceTextView.text = context.resources.getString(R.string.place_card_view_distance, place.distance.toString())
        swipeView.alpha = 1f
    }

    @Click(R.id.placeCardCoverImageView)
    fun onClick() {
        Log.d("EVENT", "profileImageView click")
    }

    @SwipeOutDirectional
    fun onSwipeOutDirectional(direction: SwipeDirection) {
        Log.d("SWIPE_OUT", "SwipeOutDirectional " + direction.name + " " + direction.direction)
        if (direction.direction == SwipeDirection.TOP.direction) {
            callback.onSwipeUp()
        }
        if (direction.direction == SwipeDirection.RIGHT_TOP.direction) {
            callback.onSwipeRight(place)
        }
    }

    @SwipeCancelState
    fun onSwipeCancelState() {
        Log.d("DEBUG", "onSwipeCancelState")
        swipeView.alpha = 1f
    }

    @SwipeInDirectional
    fun onSwipeInDirectional(direction: SwipeDirection) {
        Log.d("SwipeInDirectional", "SwipeInDirectional " + direction.name)
        callback.onSwipeRight(place)
    }

    @SwipingDirection
    fun onSwipingDirection(direction: SwipeDirection) {
        Log.d("SwipingDirection", "SwipingDirection " + direction.name)
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
        fun onSwipeRight(place: Place)
    }
}