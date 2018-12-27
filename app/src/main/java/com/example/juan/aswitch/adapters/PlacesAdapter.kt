package com.example.juan.aswitch.adapters

import android.os.Parcelable
import com.example.juan.aswitch.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.parcel.Parcelize

class PlacesAdapter(private val places: ArrayList<Place>, private val clickListener: OnClickListener) :
        RecyclerView.Adapter<PlacesAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        init {
            var imageView = itemView.findViewById<ImageView>(R.id.placeItemImage)
            name = itemView.findViewById<ImageView>(R.id.placeItemName) as TextView
        }
        fun bind(place: Place, clickListener: OnClickListener) {
            name.text = place.id
            itemView.setOnClickListener {
                clickListener.onClick(place)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): PlacesAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_place_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bind(places[position], clickListener)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = places.size

    interface OnClickListener {
        fun onClick(place: Place)
    }
}

@Parcelize
data class Place(val id: String) : Parcelable