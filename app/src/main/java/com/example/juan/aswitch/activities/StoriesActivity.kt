package com.example.juan.aswitch.activities

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import jp.shts.android.storiesprogressview.StoriesProgressView
import com.example.juan.aswitch.R
import kotlinx.android.synthetic.main.activity_stories.*
import android.util.Log
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import com.example.juan.aswitch.helpers.Stories
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.models.Story
import com.example.juan.aswitch.models.User
import com.example.juan.aswitch.services.StoriesService
import org.json.JSONObject
import java.io.File


class StoriesActivity : AppCompatActivity(), StoriesProgressView.StoriesListener {

    private var PROGRESS_COUNT: Int = 0
    private var counter = 0
    private lateinit var user: User
    private lateinit var storiesService: StoriesService
    private val resources = ArrayList<Bitmap?>()
    private lateinit var stories: ArrayList<Story>

//    private val durations = longArrayOf(50000L, 1000L, 1500L, 4000L, 5000L, 1000)

    var pressTime = 0L
    var limit = 500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storiesService = StoriesService(this)
        user = Utils.getSharedPreferencesUserObject(this)

        stories = Stories.get(intent.getIntExtra("index", 0))

        PROGRESS_COUNT = stories.size

        stories.sortBy { story -> story.seconds }
        stories.forEach { story -> resources.add(story.image) }

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_stories)

        storiesProgressView.setStoriesCount(PROGRESS_COUNT)
        storiesProgressView.setStoryDuration(5000L)
        // or
        // storiesProgressView.setStoriesCountWithDurations(durations);
        storiesProgressView.setStoriesListener(this@StoriesActivity)

        storiesProgressView.startStories(counter)

        storiesMainImage.setImageBitmap(resources[counter])
        setStoryTime(counter)
        saveStoryView(stories[counter].id)

        val reverse = storiesReverseView
        reverse.setOnClickListener{ storiesProgressView.reverse() }
        reverse.setOnTouchListener(onTouchListener)

        val skip = storiesSkipView
        skip.setOnClickListener { storiesProgressView.skip() }
        skip.setOnTouchListener(onTouchListener)

        storiesClose.setOnClickListener {
            close()
        }
    }

    private val onTouchListener = View.OnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressTime = System.currentTimeMillis()
                storiesProgressView.pause()
                return@OnTouchListener false
            }
            MotionEvent.ACTION_UP -> {
                val now = System.currentTimeMillis()
                storiesProgressView.resume()
                return@OnTouchListener limit < now - pressTime
            }
        }
        false
    }

    override fun onNext() {
        if(counter < PROGRESS_COUNT - 1) {
            Log.d("INDEX_COUNTER", counter.toString())
            val index = ++counter
            setStoryTime(index)
            saveStoryView(stories[index].id)
            storiesMainImage.setImageBitmap(resources[index])
        }
    }

    override fun onPrev() {
        if (counter - 1 < 0) return
        storiesMainImage.setImageBitmap(resources[--counter])
    }

    override fun onComplete() {
        close()
    }

    private fun close() {
        storiesProgressView.destroy()
        finish()
    }

    private fun setStoryTime(index: Int) {
        if (stories[index].seconds.compareTo(86400) == -1) {
            if (stories[index].seconds.compareTo(3600) == 1) {
                storiesTime.text = getString(
                        R.string.stories_remaining_time_h,
                        (stories[index].seconds / 3600).toString()
                )
            } else {
                storiesTime.text = getString(
                        R.string.stories_remaining_time_m,
                        (stories[index].seconds / 60).toString()
                )
            }
        } else {
            storiesTime.text = getString(
                    R.string.stories_remaining_time_d,
                    (stories[index].seconds / 86400).toString()
            )
        }
    }

    private fun saveStoryView(storyId: String) {
        val jsonObject = JSONObject()
        if(!user.role!!) {
            jsonObject.put("userId", user.id)
            storiesService.viewStory(storyId, jsonObject) {}
        }
    }
}
