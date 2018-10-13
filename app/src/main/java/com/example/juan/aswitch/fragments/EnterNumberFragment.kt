package com.example.juan.aswitch.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.juan.aswitch.R
import com.example.juan.aswitch.activities.LoginActivity
import com.example.juan.aswitch.helpers.showSnackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import kotlinx.android.synthetic.main.fragment_enter_number.*


class EnterNumberFragment : Fragment() {

    companion object {
//        lateinit var mToken :  PhoneAuthProvider.ForceResendingToken
        lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    }

    init {
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // verification completed
                Log.i("FIREBASE_AUTH", "onVerificationCompleted:" + credential)
//                showSnackbar(view!!, "onVerificationCompleted:" + credential)
                enterNumberProgressbarLoading.visibility = View.GONE
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked if an invalid request for verification is made,
                // for instance if the the phone number format is invalid.
//                showSnackbar(view!!, "onVerificationFailed")
                Log.i("FIREBASE_AUTH", "onVerificationFailed")
                enterNumberProgressbarLoading.visibility = View.GONE
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
//                    showSnackbar(view!!, "Invalid phone number.")
                    Log.i("FIREBASE_AUTH", "Invalid phone number.")
                    enterNumberProgressbarLoading.visibility = View.GONE
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.i("FIREBASE_AUTH", "Quota exceeded.")
//                    showSnackbar(view!!, "Quota exceeded.")
                    enterNumberProgressbarLoading.visibility = View.GONE
                }
                Log.i("FIREBASE_AUTH", e.toString())
            }

            override fun onCodeSent(verificationId: String?,
                                    token: PhoneAuthProvider.ForceResendingToken?) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
//                showSnackbar(view!!, "onCodeSent:" + verificationId)
                Log.i("FIREBASE_AUTH", "onCodeSent:" + verificationId)
                enterNumberProgressbarLoading.visibility = View.GONE

                // Save verification ID and resending token so we can use them later

                val arguments = Bundle()
                arguments.putString("VERIFICATION_ID", verificationId)
//                mToken ?: token

                val enterCodeFragment = EnterCodeFragment()
                enterCodeFragment.arguments = arguments

                fragmentManager?.beginTransaction()
                        ?.replace(R.id.login_frame_container, enterCodeFragment)
                        ?.addToBackStack(null)
                        ?.commit()

            }

            override fun onCodeAutoRetrievalTimeOut(verificationId: String?) {
                // called when the timeout duration has passed without triggering onVerificationCompleted
                super.onCodeAutoRetrievalTimeOut(verificationId)
            }
        }
    }

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enter_number, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enterNumberButtonContinue.setOnClickListener {
            val phoneNumber = enterNumberEditTextPhoneNumber.text.toString()

            enterNumberProgressbarLoading.visibility = View.VISIBLE

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+57 ${phoneNumber}", // Phone number to verify
                    60,                   // Timeout duration
                    TimeUnit.SECONDS,     // Unit of timeout
                    LoginActivity(),      // Activity (for callback binding)
                    mCallbacks)          // OnVerificationStateChangedCallbacks
        }
    }

}
