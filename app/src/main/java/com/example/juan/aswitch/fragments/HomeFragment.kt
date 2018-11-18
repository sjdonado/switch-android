package com.example.juan.aswitch.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.Functions
import com.example.juan.aswitch.services.UserService
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONObject

class HomeFragment : androidx.fragment.app.Fragment() {

    private lateinit var userService : UserService
    private var userObject: JSONObject = JSONObject()

    companion object {
        fun getInstance(): HomeFragment = HomeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userService = UserService(activity!!)

        val userObjectValue = Functions.getSharedPreferencesStringValue(activity!!, "USER_OBJECT")
        if(userObjectValue != null) userObject = JSONObject(userObjectValue)

        if(userObject.getBoolean("userType")) homeEditTextNotificationTitle.visibility = View.VISIBLE
        if(userObject.getBoolean("userType")) homeEditTextNotificationMessage.visibility = View.VISIBLE
        if(userObject.getBoolean("userType")) homeButtonSendNotification.visibility = View.VISIBLE

        homeButtonSendNotification.setOnClickListener {
            if(homeEditTextNotificationMessage.editText!!.text.isNotEmpty() && homeEditTextNotificationTitle.editText!!.text.isNotEmpty()) {
                val jsonObject = JSONObject()
                jsonObject.put("title", homeEditTextNotificationTitle.editText!!.text)
                jsonObject.put("message", homeEditTextNotificationMessage.editText!!.text)
                userService.sendNotification(jsonObject) {
                    Functions.showSnackbar(getView()!!, getString(R.string.alert_info_updated))
                }
            }
        }

    }
}
