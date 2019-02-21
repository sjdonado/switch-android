package com.example.juan.aswitch.fragments


import android.app.Activity
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
import com.example.juan.aswitch.models.Story
import com.example.juan.aswitch.services.StoriesService


class EditStoriesFragment : androidx.fragment.app.Fragment() {

    private lateinit var storiesService: StoriesService
    private lateinit var place: Place
    private lateinit var storiesAdapter: StoriesAdapter
    private var stories = ArrayList<Story>()
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
        place = Utils.getSharedPreferencesPlaceObject(activity!!)

//        storiesService.get(place.id) {
//            Log.d("storiesServiceUser", it.toString())
//            activity!!.runOnUiThread {
//                Utils.downloadStories(it.getJSONArray("data")) { res ->
//                    activity!!.runOnUiThread {
//                        res.forEach { story -> stories.add(story) }
//                        storiesWatchButton.show()
//                        Log.d("storiesUser", stories.toString())
//                    }
//                }
//            }
//        }

        val viewManager = LinearLayoutManager(activity!!)
        storiesAdapter = StoriesAdapter(activity!!, allStories, object: StoriesAdapter.OnClickListener {
            override fun onClick(stories: ArrayList<Story>) {
                Log.d("ON_CLICK", stories.toString())
                place.downloadedStoriesIndex = Stories.add(stories)
                Utils.openStories(activity!!, place.downloadedStoriesIndex!!)
            }
            override fun onClickDeleteButton(story: Story) {
                storiesService.delete(story.id) {
                    activity!!.runOnUiThread {
                        allStories.remove(story)
                        storiesAdapter.notifyDataSetChanged()
                    }
                }
            }
        })

        storiesService.getAll(place.id) {
            Log.d("storiesServiceAll", it.toString())
            Utils.downloadStories(place.stories!!) { res ->
                activity!!.runOnUiThread {
                    if (res.isEmpty()) {
                        storiesNotFoundTextView.visibility = View.VISIBLE
                    } else {
                        res.forEach { story -> allStories.add(story) }
                        allStories.sortBy { story -> story.seconds }
                        storiesNotFoundTextView.visibility = View.GONE
                        storiesAdapter.notifyDataSetChanged()
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

        if(!place.stories.isNullOrEmpty()) {
            Utils.downloadStories(place.stories!!) {
                activity!!.runOnUiThread {
                    Log.d("PLACE_STORIES", it.toString())
                    val index = Stories.add(it)
                    place.downloadedStoriesIndex = index
                    Log.d("PLACE_STORIES_INDEX", Stories.get(index).toString())
                    storiesWatchButton.setOnClickListener {
                        storiesWatchButton.isEnabled = false
                        Utils.openStories(activity!!, place.downloadedStoriesIndex!!)
                    }
                    storiesWatchButton.show()
                }
            }
        } else {
            storiesWatchButton.hide()
        }

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
                    storiesService.create(place.id, image) {
                        val storyJSON = it.getJSONObject("data")
                        Log.d("newStory", it.toString())
                        activity!!.runOnUiThread {
                            Utils.downloadImage(storyJSON.getJSONObject("profilePicture").getString("url")) { image ->
                                activity!!.runOnUiThread {
                                    allStories.add(Story(
                                            storyJSON.getString("id"),
                                            image,
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
}
