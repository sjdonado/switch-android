package com.example.juan.aswitch.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.HttpClient
import com.example.juan.aswitch.helpers.showSnackbar
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GetTokenResult


class LoginActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        verifyAuth()
    }

    private fun signIn() {
        val phoneConfigWithDefaultNumber = IdpConfig.PhoneBuilder()
                .setDefaultCountryIso("co")
                .build()
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(
                                Arrays.asList(phoneConfigWithDefaultNumber))
                        .build(),
                RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            when {
                resultCode == Activity.RESULT_OK -> {
                    // Successfully signed in
                    showSnackbar(login_view, "SignIn successful")
                    verifyAuth()
                    return
                }
                response == null -> {
                    // Sign in failed
                    // User pressed back button
                    showSnackbar(login_view, "Sign in cancelled")
                    return
                }
                response.error?.errorCode == ErrorCodes.NO_NETWORK -> {
                    // Sign in failed
                    //No Internet Connection
                    showSnackbar(login_view, "No Internet connection")
                    return
                }
                response.error?.errorCode == ErrorCodes.UNKNOWN_ERROR -> {
                    // Sign in failed
                    //Unknown Error
                    showSnackbar(login_view, "Unknown error")
                    return
                }
                else -> {
                    showSnackbar(login_view, "Unknown Response")
                }
            }
        }
    }

    private fun verifyAuth() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            signIn()
        } else {
            currentUser.getIdToken(true).addOnCompleteListener(object : OnCompleteListener<GetTokenResult> {
                override fun onComplete(task: Task<GetTokenResult>) {
                    if (task.isSuccessful) {
                        val idToken = task.result!!.token
                        HttpClient.TOKEN = "Bearer ${idToken!!}"
                        Log.i("TOKEN_HIJUEPUTA", idToken)
                    }
                }
            })
            startActivity(Intent(this, MenuActivity::class.java))
        }
    }

}