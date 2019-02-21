package com.example.juan.aswitch.adapters

import android.app.Activity
import com.example.juan.aswitch.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.models.Story


class StoriesAdapter(private val activity: Activity,
                     private val stories: ArrayList<Story>, private val clickListener: OnClickListener)
    : RecyclerView.Adapter<StoriesAdapter.MyViewHolder>(){

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var viewsNumber: TextView = itemView.findViewById(R.id.storyViewsRecyclerCardTextView)
        private var remainingTimeNumber: TextView = itemView.findViewById(R.id.storyRemainingTimeRecyclerCardTextView)

        private var storyViewsIcon: View = itemView.findViewById(R.id.storyViewsIconRecyclerCardView)
        private var coverImageView: View = itemView.findViewById(R.id.storyRecyclerCardCoverImageView)
        private var deleteButton: View = itemView.findViewById(R.id.storyDeleteRecyclerCardView)

        fun bind(activity: Activity, story: Story, clickListener: OnClickListener) {
            Glide.with(activity).load(story.image)
                    .apply(RequestOptions().placeholder(Utils.getCircularProgressDrawable(activity)))
                    .into(itemView.findViewById(R.id.storyRecyclerCardCoverImageView))
            viewsNumber.text = story.views.size.toString()
            if (story.seconds.compareTo(86400) == -1) {
                if (story.seconds.compareTo(3600) == 1) {
                    remainingTimeNumber.text = activity.resources.getString(
                            R.string.stories_remaining_time_h,
                            (story.seconds / 3600).toString()
                    )
                } else {
                    remainingTimeNumber.text = activity.resources.getString(
                            R.string.stories_remaining_time_m,
                            (story.seconds / 60).toString()
                    )
                }
            } else {
                storyViewsIcon.visibility = View.GONE
                remainingTimeNumber.visibility = View.GONE
                deleteButton.visibility = View.GONE
            }

            deleteButton.setOnClickListener {
                clickListener.onClickDeleteButton(story)
            }

            coverImageView.setOnClickListener {
                val stories = ArrayList<Story>()
                stories.add(story)
                clickListener.onClick(stories)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): StoriesAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.story_card_recycler_view, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(activity, stories[position], clickListener)
    }

    override fun getItemCount() = stories.size

    interface OnClickListener {
        fun onClick(stories: ArrayList<Story>)
        fun onClickDeleteButton(story: Story)
    }

}