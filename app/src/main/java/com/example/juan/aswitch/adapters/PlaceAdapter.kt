package com.example.juan.aswitch.adapters

import android.app.Activity
import com.example.juan.aswitch.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.models.Place

class PlacesAdapter(private val activity: Activity, private val places: ArrayList<Place>, private val clickListener: OnClickListener) :
        RecyclerView.Adapter<PlacesAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var name: TextView = itemView.findViewById(R.id.placeRecyclerCardNameTextView)
        private var location: TextView = itemView.findViewById(R.id.placeRecyclerCardLocationTextView)
        private var distance: TextView = itemView.findViewById(R.id.placeRecyclerCardDistanceTextView)
        private var ratingBar: RatingBar = itemView.findViewById(R.id.placeRecyclerCardDetailsRatingBar)

        fun bind(activity: Activity, place: Place, clickListener: OnClickListener) {
            Glide.with(activity).load(place.profilePicture.url)
                    .apply(RequestOptions().placeholder(Utils.getCircularProgressDrawable(activity)))
                    .into(itemView.findViewById(R.id.placeRecyclerCardCoverImageView))
            name.text = place.name
            location.text = place.location.address
            distance.text = activity.resources.getString(
                    R.string.place_card_view_distance,
                    Utils.getRoundedDistance(place.distance)
            )
            ratingBar.rating = place.rate!!.toFloat()

            itemView.setOnClickListener {
                clickListener.onClick(place)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): PlacesAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.place_card_recycler_view, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(activity, places[position], clickListener)
    }

    override fun getItemCount() = places.size

    interface OnClickListener {
        fun onClick(place: Place)
    }
}