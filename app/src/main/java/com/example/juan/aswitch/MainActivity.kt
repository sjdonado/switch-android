package com.example.juan.aswitch

import android.Manifest
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.juan.aswitch.activities.LoginActivity
import android.os.Build
import android.content.pm.PackageManager
import android.support.annotation.RequiresApi
import android.annotation.TargetApi
import android.support.v7.app.AlertDialog


class MainActivity : AppCompatActivity() {

    private val permissions = arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (arePermissionsEnabled()) {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
            } else {
                requestMultiplePermissions()
            }
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
                                .setMessage("Your error message here")
                                .setPositiveButton("Allow") { dialog, which -> requestMultiplePermissions() }
                                .setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
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

}
