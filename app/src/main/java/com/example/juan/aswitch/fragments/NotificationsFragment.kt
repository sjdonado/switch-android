package com.example.juan.aswitch.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.models.User
import com.example.juan.aswitch.services.PlaceService
import com.example.juan.aswitch.services.UserService
import kotlinx.android.synthetic.main.fragment_notifications.*
import org.json.JSONObject


class NotificationsFragment : androidx.fragment.app.Fragment() {

    private lateinit var userService: UserService
    private lateinit var placeService: PlaceService
    private lateinit var user: User

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

        user = Utils.getSharedPreferencesUserObject(activity!!)

        if(user.role!!) {
            notificationsEditTextNotificationTitle.visibility = View.VISIBLE
            notificationsEditTextNotificationMessage.visibility = View.VISIBLE
            notificationsButtonSendNotification.visibility = View.VISIBLE
        }

        notificationsButtonSendNotification.setOnClickListener {
            if(notificationsEditTextNotificationMessage.editText!!.text.isNotEmpty() && notificationsEditTextNotificationTitle.editText!!.text.isNotEmpty()) {
                val jsonObject = JSONObject()
                jsonObject.put("title", notificationsEditTextNotificationTitle.editText!!.text)
                jsonObject.put("message", notificationsEditTextNotificationMessage.editText!!.text)
                placeService.sendNotification(jsonObject) {
                    Utils.showSnackbar(getView()!!, getString(R.string.alert_info_updated))
                }
            }
        }
    }
}
