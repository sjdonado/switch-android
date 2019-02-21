package com.example.juan.aswitch.adapters

import android.app.Activity
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.juan.aswitch.R
import com.example.juan.aswitch.models.Place
import com.google.android.material.snackbar.Snackbar

abstract class PlacesSwipeAdapter(
        private val activity: Activity,
        private val view: View,
        private val places: ArrayList<Place>,
        private val onSwipeListener: OnSwipeListener
) : RecyclerView.Adapter<PlacesAdapter.MyViewHolder>() {

    private lateinit var recentlyDeletedPlace: Place
    private var recentlyDeletedPlacePosition: Int? = null
    private var mOnSwipeListener = onSwipeListener

    fun deleteItem(position: Int) {
        recentlyDeletedPlace = places[position]
        recentlyDeletedPlacePosition = position
        places.remove(recentlyDeletedPlace)
        notifyItemRemoved(position)
        Snackbar.make(view, activity.getString(R.string.starred_place_removed),
                Snackbar.LENGTH_LONG)
                .setAction(activity.getString(R.string.starred_place_removed_undo)){ undoDelete() }
                .addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        if(event != Snackbar.Callback.DISMISS_EVENT_ACTION)
                            mOnSwipeListener.onDelete(recentlyDeletedPlace)
                    }
                })
                .show()
    }

    private fun undoDelete() {
        places.add(recentlyDeletedPlacePosition!!,
                recentlyDeletedPlace)
        notifyItemInserted(recentlyDeletedPlacePosition!!)
    }

    interface OnSwipeListener {
        fun onDelete(place: Place)
    }
}