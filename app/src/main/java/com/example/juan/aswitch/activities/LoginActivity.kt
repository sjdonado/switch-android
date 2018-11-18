package com.example.juan.aswitch.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.juan.aswitch.MainActivity
import com.example.juan.aswitch.R
import com.example.juan.aswitch.fragments.UserFragment
import com.example.juan.aswitch.helpers.Functions
import com.example.juan.aswitch.services.UserService
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse

class LoginActivity : AppCompatActivity() {

    private lateinit var userService : UserService

    companion object {
        const val RC_SIGN_IN = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setSupportActionBar(login_toolbar)

        val actionBar = supportActionBar!!
        actionBar.title = getString(R.string.app_name)

        userService = UserService(this)
        if(Functions.getSharedPreferencesBooleanValue(this, "SIGN_UP")!!){
            openUserFragment()
        }else{
            signIn()
        }
    }

    private fun signIn() {
        val phoneConfigWithDefaultNumber = IdpConfig.PhoneBuilder()
                .setDefaultCountryIso(getString(R.string.country_iso_code))
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
                    Functions.showSnackbar(login_fragment_container, getString(R.string.alert_sign_in_successful))
                    Functions.setToken(this, FirebaseAuth.getInstance().currentUser) {
                        userService.getInfo { res ->
                            if(res.length() == 0) {
                                Functions.logout(this)
                                val mainActivity = Intent(this, MainActivity::class.java)
                                startActivity(mainActivity)
                            }else{
                                Functions.setSharedPreferencesStringValue(
                                        this, "USER_OBJECT", res.toString())
                                Functions.setSharedPreferencesBooleanValue(this, "SIGN_UP", true)
                                openUserFragment()
                            }
                        }
                    }
                    return
                }
                response == null -> {
                    // Sign in failed
                    // User pressed back button
                    Functions.showSnackbar(login_fragment_container, getString(R.string.alert_sign_in_canceled))
                    return
                }
                response.error?.errorCode == ErrorCodes.NO_NETWORK -> {
                    // Sign in failed
                    //No Internet Connection
                    Functions.showSnackbar(login_fragment_container, getString(R.string.internet_no_internet_connection))
                    return
                }
                response.error?.errorCode == ErrorCodes.UNKNOWN_ERROR -> {
                    // Sign in failed
                    //Unknown Error
                    Functions.showSnackbar(login_fragment_container, getString(R.string.internet_unknown_error))
                    return
                }
                else -> {
                    Functions.showSnackbar(login_fragment_container, getString(R.string.internet_unknown_response))
                }
            }
        }
    }

    private fun openUserFragment(){
        val userFragment = UserFragment()
        Functions.openFragment(this,
                R.id.login_fragment_container, userFragment)
    }

}