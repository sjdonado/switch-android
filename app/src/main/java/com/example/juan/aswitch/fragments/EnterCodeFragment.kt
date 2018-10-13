package com.example.juan.aswitch.fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.juan.aswitch.R
import com.example.juan.aswitch.activities.HomeActivity
import com.example.juan.aswitch.activities.LoginActivity
import com.example.juan.aswitch.helpers.showSnackbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.fragment_enter_code.*
import java.util.concurrent.TimeUnit

class EnterCodeFragment : Fragment() {

    var VERIFICATION_ID : String = ""
    var mAuth: FirebaseAuth? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getString("VERIFICATION_ID")?.let {
            VERIFICATION_ID = it
//            showSnackbar(view!!, VERIFICATION_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance()
        return inflater.inflate(R.layout.fragment_enter_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enterCodeEditTextCode.requestFocus()

        enterCodeButtonContinue.setOnClickListener {
            if(enterCodeEditTextCode.text.isNotEmpty()
                    && enterCodeEditTextCode.text.isNotBlank()) {
                enterCodeProgressbarLoading.visibility = View.VISIBLE
                val credential = PhoneAuthProvider.getCredential(VERIFICATION_ID,
                        enterCodeEditTextCode.text.toString()) as PhoneAuthCredential
                signInWithPhoneAuthCredential(credential)
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity(), object : OnCompleteListener<AuthResult> {
                    override fun onComplete(task: Task<AuthResult>) {
                        if (task.isSuccessful()) {
                            enterCodeProgressbarLoading.visibility = View.GONE
//                            val user = task.getResult().getUser()
                            val intent = Intent(activity, HomeActivity::class.java)
                            requireActivity().startActivity(intent)
                        } else {
                            if (task.getException() is FirebaseAuthInvalidCredentialsException) {
                                enterCodeProgressbarLoading.visibility = View.GONE
                            }
                        }
                    }
                })
    }


//    private fun resendVerificationCode(phoneNumber: String,
//                                       token: PhoneAuthProvider.ForceResendingToken?) {
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNumber, // Phone number to verify
//                60, // Timeout duration
//                TimeUnit.SECONDS, // Unit of timeout
//                LoginActivity(), // Activity (for callback binding)
//                EnterNumberFragment.mCallbacks, // OnVerificationStateChangedCallbacks
//                token)             // ForceResendingToken from callbacks
//    }
}
