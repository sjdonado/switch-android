package com.example.juan.aswitch.fragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.juan.aswitch.R
import com.example.juan.aswitch.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_users.*

class UsersFragment : Fragment() {

    lateinit var mAuth: FirebaseAuth

    companion object {
        fun newInstance(): UsersFragment = UsersFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        mAuth.currentUser?.uid?.let {
            homeTextViewName.text = it
        }

        //        mAuth.currentUser?.photoUrl?.let {
//            Glide.with(this)
//                    .load(it)
//                    .into(homeImageViewProfile)
//        }

        homeButtonLogout.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            requireActivity().startActivity(intent)
        }
    }

}
