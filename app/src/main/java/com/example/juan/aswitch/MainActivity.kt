package com.example.juan.aswitch

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.juan.aswitch.activities.LoginActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginIntent = Intent(this, LoginActivity::class.java)

        // Start the login activity.
        startActivity(loginIntent)
    }


}
