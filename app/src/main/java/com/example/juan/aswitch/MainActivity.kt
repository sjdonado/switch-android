package com.example.juan.aswitch

import com.example.juan.aswitch.R
import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.juan.aswitch.activities.LoginActivity
import android.os.Build
import android.content.pm.PackageManager
import androidx.annotation.RequiresApi
import android.annotation.TargetApi
import androidx.appcompat.app.AlertDialog
import com.example.juan.aswitch.activities.MenuActivity
import com.example.juan.aswitch.helpers.Functions
import com.example.juan.aswitch.services.UserService
import com.google.firebase.auth.FirebaseAuth
import android.app.NotificationManager
import android.app.NotificationChannel
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {

    lateinit var userService : UserService
    private val permissions = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION)

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userService = UserService(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (arePermissionsEnabled()) {
                verifyAuth()
            } else {
                requestMultiplePermissions()
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW))
        }

        FirebaseMessaging.getInstance().subscribeToTopic("switch")
                .addOnCompleteListener { task ->
                    var msg = getString(R.string.msg_subscribed)
                    if (!task.isSuccessful) {
                        msg = getString(R.string.msg_subscribe_failed)
                    }
                    Log.d("NotificationService", msg)
//                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                }

//        FirebaseInstanceId.getInstance().instanceId
//                .addOnCompleteListener(OnCompleteListener { task ->
//                    if (!task.isSuccessful) {
//                        Log.w(TAG, "getInstanceId failed", task.exception)
//                        return@OnCompleteListener
//                    }
//
//                    // Get new Instance ID token
//                    val token = task.result!!.token
//
//                    // Log and toast
//                    val msg = getString(R.string.msg_token_fmt, token)
//                    Log.d(TAG, msg)
//                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
//                })
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun arePermissionsEnabled(): Boolean {
        for (permission in permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false
        }
        return true
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun requestMultiplePermissions() {
        val remainingPermissions : ArrayList<String> = ArrayList()
        for (permission in permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                remainingPermissions.add(permission)
            }
        }
        requestPermissions(remainingPermissions.toTypedArray(), 101)
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    if (shouldShowRequestPermissionRationale(permissions[i])) {
                        AlertDialog.Builder(this)
                                .setMessage(getString(R.string.permissions_error))
                                .setPositiveButton(getString(R.string.permissions_button_allow)) { dialog, which -> requestMultiplePermissions() }
                                .setNegativeButton(getString(R.string.permissions_button_cancel)) { dialog, which -> dialog.dismiss() }
                                .create()
                                .show()
                    }
                    return
                }
            }
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    private fun verifyAuth() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        } else {
            Functions.setToken(this, currentUser) {
                userService.get { res ->
                    Functions.setSharedPreferencesStringValue(
                            this,
                            "USER_OBJECT",
                            res.getJSONObject("data").toString()
                    )
                    if(Functions.getSharedPreferencesBooleanValue(this, "SIGN_UP")!!){
                        val loginActivityIntent = Intent(this, LoginActivity::class.java)
                        startActivity(loginActivityIntent)
                    }else{
                        val menuActivityIntent = Intent(this, MenuActivity::class.java)
                        startActivity(menuActivityIntent)
                    }
                }
            }
        }
    }

}
