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
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import org.json.JSONObject
import android.content.res.Resources
import android.graphics.Point
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ProgressBar
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Priority
import com.google.firebase.auth.FirebaseAuth
import com.bumptech.glide.request.RequestOptions
import com.example.juan.aswitch.MainActivity
import com.example.juan.aswitch.fragments.UserFragment
import com.example.juan.aswitch.models.Place
import com.example.juan.aswitch.models.Time
import com.example.juan.aswitch.models.User
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.math.RoundingMode
import java.util.*

object Utils {

    private const val MOVE_DEFAULT_TIME: Long = 1000
    private const val FADE_DEFAULT_TIME: Long = 150
    private const val TAG = "Utils"
    private const val PREFERENCES_NAME = "SWITCH_DATA"
    const val NOTIFICATIONS_CHANNEL = "switch"
    const val USER_OBJECT = "USER_OBJECT"
    const val SIGN_UP = "SIGN_UP"

    fun showSnackbar (rootLayout : View, text : String ) {
        Snackbar.make(
                rootLayout,
                text,
                Snackbar.LENGTH_SHORT
        ).show()
    }

    fun setSharedPreferencesStringValue(activity: Activity, keyName : String, data: String) {
        val sp = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(keyName, data)
        editor.apply()
    }

    fun setSharedPreferencesBooleanValue(activity: Activity, keyName : String, data: Boolean) {
        val sp = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        val editor = sp.edit()
        editor.putBoolean(keyName, data)
        editor.apply()
    }

    fun getSharedPreferencesStringValue(activity: Activity, keyName: String) : String? {
        val sp = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        return sp.getString(keyName, null)
    }

    fun getSharedPreferencesBooleanValue(activity: Activity, keyName: String) : Boolean? {
        val sp = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        return sp.getBoolean(keyName, false)
    }

    fun onSharedPreferencesValue(activity: Activity, keyName: String, callback : (response: String) -> Unit) {
        val sp = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        sp.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            if(key == keyName) callback(sharedPreferences.getString(key, null)!!)
        }
    }

    fun updateSharedPreferencesObjectValue(activity: Activity, keyName: String, jsonObject: JSONObject) {
        val sp = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
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
        val sp = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        sp.edit().clear().apply()
    }

    fun logout(activity: Activity) {
        FirebaseAuth.getInstance().signOut()
        clearUserInfo(activity)
        val mainActivityIntent = Intent(activity, MainActivity::class.java)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(mainActivityIntent)
    }

    fun glideRequestOptions(activity: Activity): RequestOptions {
        return RequestOptions()
                .centerCrop()
                .placeholder(getCircularProgressDrawable(activity))
                .apply(RequestOptions.circleCropTransform())
                .priority(Priority.HIGH)
    }

    fun getCircularProgressDrawable(activity: Activity): CircularProgressDrawable {
        val circularProgressDrawable = CircularProgressDrawable(activity)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        return circularProgressDrawable
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
        return try {
            val display = windowManager.defaultDisplay
            val displayMetrics = DisplayMetrics()
            display.getMetrics(displayMetrics)
            Point(displayMetrics.widthPixels, displayMetrics.heightPixels)
        } catch (e: Exception) {
            e.printStackTrace()
            Point(0, 0)
        }
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun getGlideSize(context: Activity): Int {
        val windowY = Utils.getDisplaySize(context.windowManager).y
        return windowY - Utils.dpToPx(windowY / 6)
    }

    fun parseJSONPlace(jsonObject: JSONObject): Place {
        return Gson().fromJson(jsonObject.toString(), Place::class.java)
    }

    fun getSharedPreferencesUserObject(activity: Activity): User {
        val json = getSharedPreferencesStringValue(activity, USER_OBJECT)
        return Gson().fromJson(json, User::class.java)
    }

    fun updateSharedPreferencesUserObject(activity: Activity, user: User) {
        updateSharedPreferencesObjectValue(
                activity,
                USER_OBJECT,
                JSONObject(Gson().toJson(user).toString())
        )
    }

    fun getRoundedDistance(distance: Double): String {
        return distance.toBigDecimal().setScale(1, RoundingMode.UP).toDouble().toString()
    }

    fun copyInputStreamToFile(activity: Activity, input: InputStream, mimeType : String) : File {
        val file = File(activity.cacheDir, "${System.currentTimeMillis()/1000}.$mimeType")
        try {
            val output = FileOutputStream(file)
            try {
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                read = input.read(buffer)
                while (read != -1) {
                    output.write(buffer, 0, read)
                    read = input.read(buffer)
                }
                output.flush()
            } finally {
                output.close()
            }
        } finally {
            input.close()
        }
        return file
    }

    fun getMimeType(context: Context, uri: Uri): String? {
        val extension: String?
        //Check uri format to avoid null
        if (uri.scheme!! == ContentResolver.SCHEME_CONTENT) {
            //If scheme is a content
            val mime = MimeTypeMap.getSingleton()
            extension = mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
        return extension
    }

    fun openImagePickerIntent(fragment: Fragment, PICK_IMAGE: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        fragment.startActivityForResult(
                Intent.createChooser(intent,
                    fragment.getString(R.string.user_fragment_select_picture)), PICK_IMAGE
        )
    }

    fun showLoading(activity: Activity): Dialog {
        val progressDialog = Dialog(activity)
        val dialog = ProgressBar(activity)
        dialog.isIndeterminate = true
        dialog.visibility = View.VISIBLE
        progressDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        progressDialog.setContentView(dialog)
        progressDialog.show()
        return progressDialog
    }

    fun setChipTime(activity: Activity, openingTime: Time, closingTime: Time): String {
        val cal = Calendar.getInstance()

        val openingCal = setCalByTime(openingTime)
        val closingCal = setCalByTime(closingTime)

        when (cal.time > openingCal.time && cal.time < closingCal.time) {
            true -> return activity.resources.getString(R.string.place_details_open_chip)
            false -> return activity.resources.getString(R.string.place_details_closed_chip)
        }
    }

    private fun setCalByTime(time: Time): Calendar{
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, time.hourOfDay!!)
        cal.set(Calendar.MINUTE, time.minute!!)
        return cal
    }
}

