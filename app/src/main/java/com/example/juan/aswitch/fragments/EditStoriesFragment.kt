package com.example.juan.aswitch.fragments


import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.models.Place
import kotlinx.android.synthetic.main.fragment_edit_stories.*
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.juan.aswitch.adapters.StoriesAdapter
import com.example.juan.aswitch.helpers.Stories
import com.example.juan.aswitch.models.PlaceStory
import com.example.juan.aswitch.models.Story
import com.example.juan.aswitch.services.PlaceService
import com.example.juan.aswitch.services.StoriesService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class EditStoriesFragment : androidx.fragment.app.Fragment() {

    private lateinit var storiesService: StoriesService
    private lateinit var place: Place
    private lateinit var storiesAdapter: StoriesAdapter
    private lateinit var placeService: PlaceService
    private lateinit var progressDialog: Dialog

    private var allStories = ArrayList<Story>()

    companion object {
        fun getInstance(): EditStoriesFragment = EditStoriesFragment()
        private const val PICK_IMAGE = 1

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_stories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storiesService = StoriesService(activity!!)
        placeService = PlaceService(activity!!)
        place = Utils.getSharedPreferencesPlaceObject(activity!!)

        val viewManager = LinearLayoutManager(activity!!)
        storiesAdapter = StoriesAdapter(activity!!, allStories, object: StoriesAdapter.OnClickListener {
            override fun onClick(stories: ArrayList<Story>) {
                Utils.openStories(activity!!, Stories.add(stories))
            }
            override fun onClickDeleteButton(story: Story) {
                AlertDialog.Builder(activity!!)
                    .setTitle(getString(R.string.stories_dialog_title))
                    .setMessage(R.string.stories_dialog_message)
                    .setPositiveButton(getString(R.string.stories_dialog_positive_button)) { _, _ ->
                        storiesService.delete(story.id) { err, _ ->
                            if (!err) {
                                activity!!.runOnUiThread {
                                    allStories.remove(story)
                                    if (allStories.isNullOrEmpty()) storiesWatchButton.hide()
                                    storiesAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                    .setNeutralButton(getString(R.string.stories_dialog_neutral_button)) { dialog, _ ->
                        dialog.cancel()
                    }
                    .create()
                    .show()
            }
        })

        storiesService.getAll(place.id) { err, res ->
            if (!err) {
                Log.d("storiesServiceAll", res.toString())
                val storiesJSONArray = res.getJSONArray("data")
                val placeStories = ArrayList<PlaceStory>()
                for (i in 0..(storiesJSONArray.length() - 1)) {
                    placeStories.add(
                            Gson().fromJson(storiesJSONArray.getJSONObject(i).toString(),
                                    PlaceStory::class.java)
                    )
                }
                activity!!.runOnUiThread {
                    progressDialog = Utils.showLoading(activity!!)
                    CoroutineScope(Dispatchers.Main).launch {
                        val stories = Utils.downloadStories(placeStories)
                        if (stories.isEmpty()) {
                            storiesNotFoundTextView.visibility = View.VISIBLE
                        } else {
                            stories.forEach { story -> allStories.add(story) }
                            allStories.sortBy { story -> story.seconds }
                            storiesNotFoundTextView.visibility = View.GONE
                            storiesAdapter.notifyDataSetChanged()
                        }
                        progressDialog.hide()
                    }
                }
            }
        }

        storiesRecyclerView.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = storiesAdapter
        }

        showViewStoriesButton()

        storiesNewButton.setOnClickListener {
            Utils.openImagePickerIntent(this, PICK_IMAGE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            PICK_IMAGE -> {
                if(resultCode == Activity.RESULT_OK && data != null) {
                    val image = Utils.copyInputStreamToFile(
                            activity!!,
                            activity!!.contentResolver!!.openInputStream(data.data!!)!!,
                            Utils.getMimeType(activity!!, data.data!!)!!
                    )
                    storiesService.create(place.id, image) { err, res ->
                        if (!err) {
                            val storyJSON = res.getJSONObject("data")
                            Log.d("newStory", res.toString())
                            activity!!.runOnUiThread {
                                placeService.get { err, psRes ->
                                    if (!err) {
                                        activity!!.runOnUiThread {
                                            place.stories = Utils.parseJSONPlace(psRes.getJSONObject("data")).stories
                                            showViewStoriesButton()
                                        }
                                    }
                                }
                                runBlocking {
                                    allStories.add(0, Story(
                                            storyJSON.getString("id"),
                                            Utils.downloadImage(storyJSON.getJSONObject("profilePicture").getString("url")),
                                            ArrayList(),
                                            storyJSON.getInt("seconds")))
                                    storiesAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                Log.e("INTENT", "Unrecognized request code")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        storiesWatchButton.isEnabled = true
    }

    override fun onDestroy() {
        super.onDestroy()
        Stories.clear()
    }

    private fun showViewStoriesButton() {
        if(!place.stories.isNullOrEmpty()) {
            Stories.clear()
            CoroutineScope(Dispatchers.Main).launch {
                val stories = Utils.downloadStories(place.stories!!)
                Log.d("PLACE_STORIES", stories.toString())
                val index = Stories.add(stories)
                place.downloadedStoriesIndex = index
                Log.d("PLACE_STORIES_INDEX", Stories.get(index).toString())
                storiesWatchButton.setOnClickListener {
                    storiesWatchButton.isEnabled = false
                    Utils.openStories(activity!!, place.downloadedStoriesIndex!!)
                }
                storiesWatchButton.show()
            }
        } else {
            storiesWatchButton.hide()
        }
    }
}
