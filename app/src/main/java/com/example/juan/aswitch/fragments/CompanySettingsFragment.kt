package com.example.juan.aswitch.fragments


import android.content.Context
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import androidx.preference.R.id.checkbox

import com.example.juan.aswitch.R
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.example.juan.aswitch.helpers.Functions
import com.example.juan.aswitch.services.PlaceService
import org.json.JSONArray
import org.json.JSONObject


class CompanySettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val KEY_LABELS = "key_labels"
    private lateinit var placeService: PlaceService
    private var placeObject: JSONObject = JSONObject()

    companion object {
        fun getInstance(): CompanySettingsFragment = CompanySettingsFragment()
    }

//    override fun onStart() {
//        super.onStart()
//        val placeObjectValue = Functions.getSharedPreferencesStringValue(activity!!, "PLACE_OBJECT")
//        if(placeObjectValue != null) placeObject = JSONObject(placeObjectValue)
//        if (!placeObject.isNull("labels")) {
//            val editor = preferenceManager.sharedPreferences.edit()
//            editor.putStringSet(KEY_LABELS, Functions.toStringArray(placeObject.getJSONArray("labels"))!!.toMutableSet())
//            editor.apply()
//        }
//    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.company_settings, rootKey)
        placeService = PlaceService(activity!!)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            KEY_LABELS -> {
                val selections = sharedPreferences!!.getStringSet(key, null)
                val jsonObject = JSONObject()
                jsonObject.put("labels", JSONArray(selections!!.toTypedArray()))
                placeService.update(jsonObject) {res ->
                    Functions.updateSharedPreferencesObjectValue(activity!!, "PLACE_OBJECT", res)
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
