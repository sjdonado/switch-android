package com.example.juan.aswitch.helpers

import com.example.juan.aswitch.R
import com.google.android.material.snackbar.Snackbar
import android.transition.Fade
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.transition.TransitionInflater
import android.transition.TransitionSet
import android.view.View
import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import org.json.JSONObject
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Priority
import com.google.firebase.auth.FirebaseAuth
import com.bumptech.glide.request.RequestOptions
import org.json.JSONArray

object Utils {

    private const val MOVE_DEFAULT_TIME: Long = 1000
    private const val FADE_DEFAULT_TIME: Long = 150
    private const val TAG = "Utils"

    fun showSnackbar (rootLayout : View, text : String ) {
        Snackbar.make(
                rootLayout,
                text,
                Snackbar.LENGTH_SHORT
        ).show()
    }

    fun openFragment(activity: AppCompatActivity, fragment_container: Int, fragment: androidx.fragment.app.Fragment) {

        val exitFade = Fade()
        exitFade.duration = FADE_DEFAULT_TIME
        val previousFragment = activity.supportFragmentManager.findFragmentById(fragment_container)
        if(previousFragment is Fragment) previousFragment.exitTransition = exitFade

        val enterTransitionSet = TransitionSet()
        enterTransitionSet.addTransition(TransitionInflater.from(activity).inflateTransition(android.R.transition.move))
        enterTransitionSet.duration = MOVE_DEFAULT_TIME
        enterTransitionSet.startDelay = FADE_DEFAULT_TIME
        fragment.sharedElementEnterTransition = enterTransitionSet

        val enterFade = Fade()
        enterFade.startDelay = FADE_DEFAULT_TIME
        enterFade.duration = FADE_DEFAULT_TIME
        fragment.enterTransition = enterFade

        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun setSharedPreferencesStringValue(activity: Activity, keyName : String, data: String) {
        val sp = activity.getSharedPreferences("SWITCH_DATA", MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(keyName, data)
        editor.apply()
    }

    fun setSharedPreferencesBooleanValue(activity: Activity, keyName : String, data: Boolean) {
        val sp = activity.getSharedPreferences("SWITCH_DATA", MODE_PRIVATE)
        val editor = sp.edit()
        editor.putBoolean(keyName, data)
        editor.apply()
    }

    fun getSharedPreferencesStringValue(activity: Activity, keyName: String) : String? {
        val sp = activity.getSharedPreferences("SWITCH_DATA", MODE_PRIVATE)
        return sp.getString(keyName, null)
    }

    fun getSharedPreferencesBooleanValue(activity: Activity, keyName: String) : Boolean? {
        val sp = activity.getSharedPreferences("SWITCH_DATA", MODE_PRIVATE)
        return sp.getBoolean(keyName, false)
    }

    fun onSharedPreferencesValue(activity: Activity, keyName: String, callback : (response: String) -> Unit) {
        val sp = activity.getSharedPreferences("SWITCH_DATA", MODE_PRIVATE)
        sp.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            if(key == keyName) callback(sharedPreferences.getString(key, null)!!)
        }
    }

    fun updateSharedPreferencesObjectValue(activity: Activity, keyName: String, jsonObject: JSONObject) {
        val sp = activity.getSharedPreferences("SWITCH_DATA", MODE_PRIVATE)
        val oldJsonObjectValue = sp.getString(keyName, null)

        if(oldJsonObjectValue != null) {
            val newJsonObject = merge(arrayListOf(JSONObject(oldJsonObjectValue), jsonObject))
            setSharedPreferencesStringValue(activity, keyName, newJsonObject.toString())
        } else {
            setSharedPreferencesStringValue(activity, keyName, jsonObject.toString())
        }
    }

    fun merge(objects: List<JSONObject>): JSONObject {
        var i = 0
        var j = 1
        while (i < objects.size - 1) {
            merge(objects[i], objects[j])
            i++
            j++
        }
        return objects[objects.size - 1]
    }

    private fun merge(j1: JSONObject, j2: JSONObject) {
        val keys = j1.keys()
        var obj1: Any
        var obj2: Any
        while (keys.hasNext()) {
            val next = keys.next()
            if (j1.isNull(next)) continue
            obj1 = j1.get(next)
            if (!j2.has(next)) j2.putOpt(next, obj1)
            obj2 = j2.get(next)
            if (obj1 is JSONObject && obj2 is JSONObject) {
                merge(obj1, obj2)
            }
        }
    }

    fun setToken(activity: Activity, currentUser: FirebaseUser?, callback: () -> Unit) {
        currentUser!!.getIdToken(true).addOnCompleteListener(object : OnCompleteListener<GetTokenResult> {
            override fun onComplete(task: Task<GetTokenResult>) {
                if (task.isSuccessful) {
                    val idToken = task.result!!.token
                    setSharedPreferencesStringValue(activity, "USER_TOKEN", "Bearer ${idToken!!}")
                    callback()
                }
            }
        })
    }

    private fun clearUserInfo(activity: Activity){
        val sp = activity.getSharedPreferences("SWITCH_DATA", MODE_PRIVATE)
        sp.edit().clear().apply()
    }

    fun logout(activity: Activity) {
        FirebaseAuth.getInstance().signOut()
        clearUserInfo(activity)
    }

    fun glideRequestOptions(activity: Activity): RequestOptions {
        val circularProgressDrawable = CircularProgressDrawable(activity)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        return RequestOptions()
                .centerCrop()
                .placeholder(circularProgressDrawable)
                .apply(RequestOptions.circleCropTransform())
                .priority(Priority.HIGH)
    }

    fun toStringArray(array: JSONArray?): Array<String?>? {
        if (array == null)
            return null
        val arr = arrayOfNulls<String>(array.length())
        for (i in arr.indices) {
            arr[i] = array.optString(i)
        }
        return arr
    }

    fun getDisplaySize(windowManager: WindowManager): Point {
        try {
            if (Build.VERSION.SDK_INT > 16) {
                val display = windowManager.defaultDisplay
                val displayMetrics = DisplayMetrics()
                display.getMetrics(displayMetrics)
                return Point(displayMetrics.widthPixels, displayMetrics.heightPixels)
            } else {
                return Point(0, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Point(0, 0)
        }

    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}

