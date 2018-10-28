package com.example.juan.aswitch.fragments


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.juan.aswitch.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_users.*
import android.provider.MediaStore
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.juan.aswitch.activities.LoginActivity
import com.example.juan.aswitch.helpers.HttpClient
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.DefaultCallback
import java.io.*
import java.net.URI
import java.io.File.separator




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
//            EasyImage.openChooserWithGallery(this, "Select an picture", PICK_IMAGE)
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }

        homeButtonLogout.setOnClickListener { view ->
//            HttpClient.get("/users/all") { response ->
//                Log.i("RESPONSE", response)
//            }
            mAuth.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            requireActivity().startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            PICK_IMAGE ->{
                if(resultCode == Activity.RESULT_OK && data != null) {
                    val image = copyInputStreamToFile(activity!!.contentResolver!!.openInputStream(data.data!!)!!, "test.png")
                    HttpClient.uploadImage("/users", "profile_picture", image) { response ->
                        Log.i("RESPONSE", response)
                    }
                    Glide.with(this)
                            .load(image)
                            .into(homeImageViewProfilePicture)
                }
            }
            else -> {
                Toast.makeText(context,"Unrecognized request code", Toast.LENGTH_SHORT)
            }
        }
    }


    private fun copyInputStreamToFile(input: InputStream, fileName : String) : File {
        val file = File(activity!!.cacheDir, fileName)
        try {
            val output = FileOutputStream(file)
            try {
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                read = input.read(buffer)
                while (read != -1) {
                    output.write(buffer, 0, read)
                    read = input.read(buffer)
                }
                output.flush()
            } finally {
                output.close()
            }
        } finally {
            input.close()
        }
        return file
    }

}
