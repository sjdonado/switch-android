package com.example.juan.aswitch.helpers

import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View

open class Functions {
    companion object {
        fun showSnackbar (rootLayout : View, text : String ) {
            Snackbar.make(
                    rootLayout,
                    text,
                    Snackbar.LENGTH_SHORT
            ).show()
        }

        fun openFragment(activity: AppCompatActivity, fragment_id : Int, fragment: Fragment) {
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.replace(fragment_id, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}

