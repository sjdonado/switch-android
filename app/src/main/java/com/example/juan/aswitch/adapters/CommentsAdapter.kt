package com.example.juan.aswitch.adapters

import android.app.Activity
import android.util.Log
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
import com.example.juan.aswitch.models.Comment


class CommentsAdapter(private val activity: Activity, private val comments: ArrayList<Comment>)
    : RecyclerView.Adapter<CommentsAdapter.MyViewHolder>(){

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var userName: TextView = itemView.findViewById(R.id.commentUseNameTextView)
        private var commentValue: TextView = itemView.findViewById(R.id.commentValueTextView)
        private var qualify: RatingBar = itemView.findViewById(R.id.commentQualifyRatingBar)

        fun bind(activity: Activity, comment: Comment) {
            Glide.with(activity).load(comment.profilePicture.url)
                    .apply(RequestOptions().placeholder(Utils.getCircularProgressDrawable(activity)))
                    .into(itemView.findViewById(R.id.commentProfilePictureImageView))
            userName.text = comment.name
            commentValue.text = comment.value
            qualify.rating = comment.qualify.toFloat()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CommentsAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.comment_card_recycler_view, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(activity, comments[position])
    }

    override fun getItemCount() = comments.size

}