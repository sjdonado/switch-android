package com.example.juan.aswitch.models

import android.app.Activity
import com.example.juan.aswitch.helpers.Functions
import org.json.JSONObject
import kotlin.properties.Delegates

class User (activity: Activity){

    var activity : Activity by Delegates.notNull()

    companion object {
        lateinit var userObject : JSONObject
    }

    init {
       this.activity = activity
        Functions.onSharedPreferencesValue(this.activity, "USER_OBJECT") { res ->
            userObject = JSONObject(res)
        }
    }


}