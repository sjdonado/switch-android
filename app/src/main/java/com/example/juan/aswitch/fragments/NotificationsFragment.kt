package com.example.juan.aswitch.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.Functions
import com.example.juan.aswitch.services.PlaceService
import com.example.juan.aswitch.services.UserService
import kotlinx.android.synthetic.main.fragment_notifications.*
import org.json.JSONObject


class NotificationsFragment : androidx.fragment.app.Fragment() {

    private lateinit var userService: UserService
    private lateinit var placeService: PlaceService
    private var userObject: JSONObject = JSONObject()

    companion object {
        fun getInstance(): NotificationsFragment = NotificationsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userService = UserService(activity!!)
        placeService = PlaceService(activity!!)

        val userObjectValue = Functions.getSharedPreferencesStringValue(activity!!, "USER_OBJECT")
        if(userObjectValue != null) userObject = JSONObject(userObjectValue)

        if(userObject.getBoolean("role")) notificationsEditTextNotificationTitle.visibility = View.VISIBLE
        if(userObject.getBoolean("role")) notificationsEditTextNotificationMessage.visibility = View.VISIBLE
        if(userObject.getBoolean("role")) notificationsButtonSendNotification.visibility = View.VISIBLE

        notificationsButtonSendNotification.setOnClickListener {
            if(notificationsEditTextNotificationMessage.editText!!.text.isNotEmpty() && notificationsEditTextNotificationTitle.editText!!.text.isNotEmpty()) {
                val jsonObject = JSONObject()
                jsonObject.put("title", notificationsEditTextNotificationTitle.editText!!.text)
                jsonObject.put("message", notificationsEditTextNotificationMessage.editText!!.text)
                placeService.sendNotification(jsonObject) {
                    Functions.showSnackbar(getView()!!, getString(R.string.alert_info_updated))
                }
            }
        }
    }
}
