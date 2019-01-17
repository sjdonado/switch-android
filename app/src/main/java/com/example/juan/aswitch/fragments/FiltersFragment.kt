package com.example.juan.aswitch.fragments


import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.FragmentHandler
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.services.UserService
import kotlinx.android.synthetic.main.fragment_filters.*
import org.json.JSONObject


class FiltersFragment : BaseFragment() {
    override fun getTitle(): String {
        return "Filters"
    }

    private var restaurantSelected = false
    private var barSelected = false
    private lateinit var userService: UserService
    private var userObject: JSONObject = JSONObject()
    private lateinit var fragmentHandler: FragmentHandler


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

        val userObjectValue = Utils.getSharedPreferencesStringValue(
                activity!!,
                Utils.USER_OBJECT
        )

        if(userObjectValue != null) {
            userObject = JSONObject(userObjectValue)
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
            R.id.doneAction -> {
                if(radiusFilter.progress != userObject.getInt("radius")) {
                    userObject.put("radius", radiusFilter.progress)
                    Utils.updateSharedPreferencesObjectValue(
                            activity!!,
                            Utils.USER_OBJECT,
                            userObject
                    )
                }
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
}
