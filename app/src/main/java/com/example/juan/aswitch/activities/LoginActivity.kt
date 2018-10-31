package com.example.juan.aswitch.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.juan.aswitch.R
import com.example.juan.aswitch.fragments.UserFragment
import com.example.juan.aswitch.helpers.Functions
import com.example.juan.aswitch.helpers.HttpClient
import com.example.juan.aswitch.services.UserService
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import kotlinx.android.synthetic.main.activity_menu.*

class LoginActivity : AppCompatActivity() {

    lateinit var userService : UserService

    companion object {
        const val RC_SIGN_IN = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        userService = UserService(this, login_progress_bar)

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
                    Functions.showSnackbar(login_fragment_container, "SignIn successful")
                    setToken(FirebaseAuth.getInstance().currentUser) {
                        userService.signUp("/") { response ->
                            val userFragment = UserFragment().apply {
                                arguments = Bundle().apply {
                                    putBoolean("signUp", true)
                                }
                            }
                            Functions.openFragment(this, R.id.login_fragment_container, userFragment)
                        }
                    }
                    return
                }
                response == null -> {
                    // Sign in failed
                    // User pressed back button
                    Functions.showSnackbar(login_fragment_container, "Sign in cancelled")
                    return
                }
                response.error?.errorCode == ErrorCodes.NO_NETWORK -> {
                    // Sign in failed
                    //No Internet Connection
                    Functions.showSnackbar(login_fragment_container, "No Internet connection")
                    return
                }
                response.error?.errorCode == ErrorCodes.UNKNOWN_ERROR -> {
                    // Sign in failed
                    //Unknown Error
                    Functions.showSnackbar(login_fragment_container, "Unknown error")
                    return
                }
                else -> {
                    Functions.showSnackbar(login_fragment_container, "Unknown Response")
                }
            }
        }
    }

    private fun verifyAuth() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            signIn()
        } else {
            setToken(currentUser) {
                startActivity(Intent(this, MenuActivity::class.java))
            }
        }
    }

    private fun setToken(currentUser: FirebaseUser?, callback: () -> Unit) {
        currentUser!!.getIdToken(true).addOnCompleteListener(object : OnCompleteListener<GetTokenResult> {
            override fun onComplete(task: Task<GetTokenResult>) {
                if (task.isSuccessful) {
                    val idToken = task.result!!.token
                    HttpClient.TOKEN = "Bearer ${idToken!!}"
                    callback()
                }
            }
        })
    }

}