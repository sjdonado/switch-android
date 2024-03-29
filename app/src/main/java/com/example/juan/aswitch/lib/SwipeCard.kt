package com.example.juan.aswitch.lib

import android.app.Activity
import com.example.juan.aswitch.R
import android.graphics.Point
import android.util.Log
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.models.Place
import com.google.android.material.chip.Chip
import com.mindorks.placeholderview.SwipeDirection
import com.mindorks.placeholderview.annotations.*
import com.mindorks.placeholderview.annotations.swipe.*
import kotlinx.android.synthetic.main.fragment_place_details.*
import kotlin.math.sqrt

@Layout(R.layout.place_card_view)
class SwipeCard(private val context: Activity,
                private val place: Place,
                private val cardViewHolderSize: Point,
                private val callback: Callback) {

    @View(R.id.placeCardCoverImageView)
    lateinit var placeCardCoverImageView: ImageView

    @View(R.id.placeCardRatingBar)
    lateinit var placeCardRatingBar: RatingBar

    @View(R.id.placeCardRatingTextView)
    lateinit var placeCardRatingTextView: TextView

    @View(R.id.placeCardRatingSizeTextView)
    lateinit var placeCardRatingSizeTextView: TextView

    @View(R.id.placeCardNameTextView)
    lateinit var placeCardNameTextView: TextView

    @View(R.id.placeCardLocationTextView)
    lateinit var placeCardLocationTextView: TextView

    @View(R.id.placeCardDistanceTextView)
    lateinit var placeCardDistanceTextView: TextView

    @View(R.id.placeTimeChip)
    lateinit var placeTimeChip: Chip

    @SwipeView
    lateinit var swipeView: android.view.View

    @JvmField
    @Position
    var position: Int = 0

    @Resolve
    fun onResolved() {
        val size = Utils.getGlideSize(context)
        Glide.with(context).load(place.profilePicture.url)
                .apply(RequestOptions().placeholder(Utils.getCircularProgressDrawable(context)))
                .apply(RequestOptions().override(size, size))
                .into(placeCardCoverImageView)
        placeCardNameTextView.text = place.name
        placeCardLocationTextView.text = place.location.address
        placeCardDistanceTextView.text = context.resources.getString(
                R.string.place_details_distance,
                Utils.getRoundedDistance(place.distance)
        )
        placeCardRatingBar.rating = place.rate!!.value.toFloat()
        placeCardRatingTextView.text = place.rate!!.value.toString()
        placeCardRatingSizeTextView.text = context.resources.getString(
                R.string.place_details_rate_size,
                place.rate!!.size.toString()
        )
        placeTimeChip.text = Utils.setChipTime(context, place.openingTime!!, place.closingTime!!)
        swipeView.alpha = 1f
    }

    @Click(R.id.placeCardCoverImageView)
    fun onClick() {
        Log.d("EVENT", "profileImageView click")
        callback.onCoverClick(place)
    }

    @SwipeOutDirectional
    fun onSwipeOutDirectional(direction: SwipeDirection) {
        Log.d("SWIPE_OUT", "SwipeOutDirectional " + direction.name + " " + direction.direction)
        if (direction.direction == SwipeDirection.TOP.direction) {
            callback.onSwipeUp()
        }
        if (direction.direction == SwipeDirection.RIGHT_TOP.direction) {
            callback.onSwipeAccept(place)
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
        callback.onSwipeAccept(place)
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
        fun onSwipeAccept(place: Place)
        fun onCoverClick(place: Place)
    }
}