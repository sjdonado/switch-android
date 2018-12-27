package com.example.juan.aswitch.fragments


import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat
import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.Functions
import com.example.juan.aswitch.services.UserService
import org.json.JSONObject


class UserSettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val KEY_DISTANCE = "key_distance"
    private lateinit var userService: UserService

    companion object {
        fun getInstance(): UserSettingsFragment = UserSettingsFragment()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.user_settings, rootKey)
        userService = UserService(activity!!)
//        val seekbar = findPreference("key_distance") as SeekBar
//        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//        })
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            KEY_DISTANCE -> {
//                Toast.makeText(context!!, sharedPreferences!!.getInt(key, 0).toString(), Toast.LENGTH_SHORT).show()
                val jsonObject = JSONObject()
                jsonObject.put("distance", sharedPreferences!!.getInt(key, 0).toString())
                userService.update(jsonObject) {res ->
                    Functions.updateSharedPreferencesObjectValue(
                            activity!!,
                            "USER_OBJECT",
                            res.getJSONObject("data")
                    )
                }
//                mTask = CheckUpdateTask.getInstance(false)
//                if (!mTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
//                    mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, activity)
//                }
            }
            else -> {
//                val link = LinkConfig.getInstance().findLink(key, activity)
//                if (link != null) {
//                    OTAUtils.launchUrl(link!!.getUrl(), activity)
//                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

    }

    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

}
