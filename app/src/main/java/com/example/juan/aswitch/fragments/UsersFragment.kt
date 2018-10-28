package com.example.juan.aswitch.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.example.juan.aswitch.R
import com.example.juan.aswitch.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_users.*
import com.example.juan.aswitch.helpers.HttpClient
import com.squareup.picasso.Picasso
import java.io.File


class UsersFragment : Fragment() {

    lateinit var mAuth: FirebaseAuth

    companion object {
        fun newInstance(): UsersFragment = UsersFragment()
        private val PICK_IMAGE = 0
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        homeImageViewProfilePicture.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }

        homeButtonLogout.setOnClickListener { view ->
            HttpClient.get("/users/all") { response ->
                Log.i("RESPONSE", response)
            }
//            mAuth.signOut()
//            val intent = Intent(activity, LoginActivity::class.java)
//            requireActivity().startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            PICK_IMAGE ->{
                if(resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.path.let {
                        val image = File(it)
                        Log.i("IMAGE_FILE", image.extension)
                        HttpClient.uploadImage("/users", "profile_picture", image) {
                            Log.i("RESPONSE", it.toString())
                        }
                    }
                    Picasso.get().load(data.dataString).into(homeImageViewProfilePicture)
                }
            }
            else -> {
                Toast.makeText(context,"Unrecognized request code",Toast.LENGTH_SHORT)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
