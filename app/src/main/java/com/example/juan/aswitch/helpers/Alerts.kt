package com.example.juan.aswitch.helpers

import android.support.design.widget.Snackbar
import android.view.View

fun showSnackbar (rootLayout : View, text : String ) {
    Snackbar.make(
            rootLayout, // Parent view
            text, // Message to show
            Snackbar.LENGTH_SHORT // How long to display the message.
    ).show()
}