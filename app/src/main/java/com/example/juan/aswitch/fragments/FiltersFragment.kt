package com.example.juan.aswitch.fragments


import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.FragmentHandler
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.models.User
import com.example.juan.aswitch.services.PlaceService
import com.example.juan.aswitch.services.UserService
import com.google.android.material.chip.Chip
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_filters.*
import org.json.JSONObject
import org.json.JSONArray


class FiltersFragment : BaseFragment() {

    override fun getTitle(): String {
        return "Filters"
    }

    private lateinit var userService: UserService
    private lateinit var placeService: PlaceService
    private lateinit var user: User
    private lateinit var fragmentHandler: FragmentHandler
    private var filtersArrayList = ArrayList<String>()

    companion object {
        fun getInstance(): FiltersFragment = FiltersFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filters, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentHandler = FragmentHandler(activity!! as AppCompatActivity, R.id.menu_fragment_container)

        userService = UserService(activity!!)
        placeService = PlaceService(activity!!)


        user = Utils.getSharedPreferencesUserObject(activity!!)
        filterRadiusSeekBar.progress = user.radius!!
        filtersArrayList = user.filters!!

        filtersArrayList.forEach {
            filterUserChipGroup.addView(createUserChip(it))
        }

        filterAddFilterButton.setOnClickListener {
            showCategoriesChips(filterCategoriesGroupsTextView.visibility == View.GONE)
        }

        placeService.getCategoriesGroups {
            val data = it.getJSONObject("data")
            Log.d("ARRAY", data.toString())
            activity!!.runOnUiThread {
                val keys = data.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    if (data.get(key) is JSONArray) {
                        val groupChip = createChip(key)
                        groupChip.setOnClickListener {
                            if (groupChip.isChecked) {
                                val categories = data!!.get(key) as JSONArray
                                Log.d("CATEGORIES", categories.toString())
                                filterCategoriesChipGroup.removeAllViews()
                                for(i in 0..(categories.length() - 1)) {
                                    val category = (categories[i] as JSONObject).getString("subsegment")
                                    if(!filtersArrayList.contains(category)) {
                                        val categoryChip = createChip(category)
                                        categoryChip.setOnClickListener {
                                            filtersArrayList.add(category)
                                            filterUserChipGroup.addView(createUserChip(category))
                                            filterCategoriesGroupsChipGroup.clearCheck()
                                            filterCategoriesChipGroup.clearCheck()
                                            showCategoriesChips(false)
                                        }
                                        filterCategoriesChipGroup.addView(categoryChip)
                                    }
                                }
//                                filterScrollView.smoothScrollTo(0, filterAddFilterButton.bottom)
                            } else {
                                filterCategoriesChipGroup.removeAllViews()
                            }
                        }
                        filterCategoriesGroupsChipGroup.addView(groupChip)
                    }
                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.doneAction -> {
                user.radius = filterRadiusSeekBar.progress
                user.filters = filtersArrayList
                Utils.updateSharedPreferencesUserObject(activity!!, user)
                fragmentHandler.add(SwipeFragment.getInstance())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.done_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun createChip(text: String): Chip {
        val chip = Chip(filterCategoriesGroupsChipGroup.context)
        chip.text= text
        chip.isClickable = true
        chip.isCheckable = true
        return chip
    }

    private fun createUserChip(text: String): Chip {
        val userChip = createChip(text)
        userChip.isCloseIconVisible = true
        userChip.setOnCloseIconClickListener {
            filterUserChipGroup.removeView(userChip)
            filtersArrayList.remove(text)
        }
        return userChip
    }

    private fun showCategoriesChips(show: Boolean) {
        if (show) {
            filterCategoriesGroupsTextView.visibility = View.VISIBLE
            filterCategoriesGroupsChipGroup.visibility = View.VISIBLE
            filterCategoriesTextView.visibility = View.VISIBLE
            filterCategoriesChipGroup.visibility = View.VISIBLE
        } else {
            filterCategoriesGroupsTextView.visibility = View.GONE
            filterCategoriesGroupsChipGroup.visibility = View.GONE
            filterCategoriesTextView.visibility = View.GONE
            filterCategoriesChipGroup.visibility = View.GONE
        }
    }
}
