package com.example.juan.aswitch.fragments


import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.Functions
import com.example.juan.aswitch.services.UserService
import kotlinx.android.synthetic.main.fragment_filters.*
import org.json.JSONObject


class FiltersFragment : androidx.fragment.app.Fragment() {

    private var restaurantSelected = false
    private var barSelected = false
    private lateinit var userService: UserService
    private var filtersObject: JSONObject = JSONObject()

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

        userService = UserService(activity!!)

        val userObjectValue = Functions.getSharedPreferencesStringValue(
                activity!!,
                "USER_OBJECT"
        )
        if(userObjectValue != null) {
            val userObject = JSONObject(userObjectValue)
            radiusFilter.progress = userObject.getInt("radius")
        }


        filterButtonRestaurant.setOnClickListener {
            restaurantSelected = selectFilter(restaurantSelected, it)
        }

        filterButtonBar.setOnClickListener {
            barSelected = selectFilter(barSelected, it)
        }
    }

    private fun selectFilter(isSelected: Boolean, view: View): Boolean {
        val background = view.background as GradientDrawable
        if(isSelected) {
            background.setColor(getColor(context!!, R.color.colorPrimaryDark))
        }else{
            background.setColor(getColor(context!!, R.color.colorAccent))
        }
        return !isSelected
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.done_action -> {
                filtersObject.put("radius", radiusFilter.progress)
                userService.update(filtersObject) {res ->
                    Functions.updateSharedPreferencesObjectValue(
                            activity!!,
                            "USER_OBJECT",
                            res.getJSONObject("data")
                    )
                }
                Functions.openFragment(
                        activity as AppCompatActivity,
                        R.id.menu_fragment_container,
                        HomeFragment.getInstance()
                )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.done_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
