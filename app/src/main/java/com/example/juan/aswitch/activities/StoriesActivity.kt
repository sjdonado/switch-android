package com.example.juan.aswitch.activities

import android.content.Intent
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
import com.example.juan.aswitch.models.Story
import java.io.File


class StoriesActivity : AppCompatActivity(), StoriesProgressView.StoriesListener {

    private var PROGRESS_COUNT: Int = 0
    private var counter = 0
    private lateinit var resources: Array<Bitmap?>

//    private val durations = longArrayOf(50000L, 1000L, 1500L, 4000L, 5000L, 1000)

    var pressTime = 0L
    var limit = 500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val storiesArray = intent.getParcelableArrayListExtra<Story>("stories")

        PROGRESS_COUNT = storiesArray.size
        resources = arrayOfNulls(PROGRESS_COUNT)

        storiesArray.forEach { story ->
            resources[story.index] = BitmapFactory.decodeFile(File(story.path).absolutePath)
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_stories)

        storiesProgressView.setStoriesCount(PROGRESS_COUNT)
        storiesProgressView.setStoryDuration(5000L)
        // or
        // storiesProgressView.setStoriesCountWithDurations(durations);
        storiesProgressView.setStoriesListener(this@StoriesActivity)

        storiesProgressView.startStories(counter)

        storiesMainImage.setImageBitmap(resources[counter])

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
        if(counter < resources.size - 1) storiesMainImage.setImageBitmap(resources[++counter])
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
}
