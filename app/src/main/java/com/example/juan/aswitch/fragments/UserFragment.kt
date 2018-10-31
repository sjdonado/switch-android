package com.example.juan.aswitch.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.juan.aswitch.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_users.*
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.json.JSONObject
import java.io.*
import android.webkit.MimeTypeMap
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.juan.aswitch.activities.MenuActivity
import com.example.juan.aswitch.helpers.Functions
import com.example.juan.aswitch.services.UserService
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.navigation_header.*


class UserFragment : androidx.fragment.app.Fragment() {

    lateinit var mAuth: FirebaseAuth
    lateinit var userService : UserService
    private var signUp = false

    companion object {
        fun getInstance(): UserFragment = UserFragment()
        private val PICK_IMAGE = 0
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getBoolean("signUp")?.let {
            signUp = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        userService = UserService(activity!!)

        val userObject = JSONObject(arguments!!.getString("userObject"))


        if(!userObject.getString("profile_picture").isNullOrEmpty()) {
            Glide.with(activity!!)
                    .load(userObject.getString("profile_picture"))
                    .into(userImageViewProfilePicture)
        }

        if(!userObject.getString("name").isNullOrEmpty()) userEditTextName.editText!!.setText(userObject.getString("name"))
        if(!userObject.getString("email").isNullOrEmpty()) userEditTextEmail.editText!!.setText(userObject.getString("email"))

        if (signUp){
            userButtonAction.text = "Next"
        } else {
            userButtonAction.text = "Save"
//            userService.getInfo("/") { res ->
//                if(res.toString().contains("profile_picture")) {
//                    activity!!.runOnUiThread {
//                        Glide.with(activity)
//                                .load(res.getString("profile_picture"))
//                                .into(userImageViewProfilePicture)
//                        if(res.toString().contains("name")) userEditTextName.editText!!.setText(res.getString("name"))
//                        if(res.toString().contains("email")) userEditTextEmail.editText!!.setText(res.getString("email"))
//                    }
//                }
//            }
        }

        userImageViewProfilePicture.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }

        userButtonAction.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("name", userEditTextName.editText!!.text)
            jsonObject.put("email", userEditTextEmail.editText!!.text)
            userService.put("/", jsonObject) {
                Functions.showSnackbar(getView()!!, "Info updated!")
            }
            if(signUp) requireActivity().startActivity(Intent(activity!!, MenuActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            PICK_IMAGE ->{
                if(resultCode == Activity.RESULT_OK && data != null) {
                    Log.i("DATA_URI", data.data!!.path)
                    val image = copyInputStreamToFile(
                            activity!!.contentResolver!!.openInputStream(data.data!!)!!,
                            getMimeType(activity!!, data.data!!)!!)
                    userService.uploadImage("/upload", "profile_picture", image) { res ->
                        Functions.showSnackbar(view!!, "Info updated!")
                        activity!!.runOnUiThread {
                            Log.i("PROFILE_PICTURE_URL", res.getString("profile_picture"))
                            Glide.get(activity!!.applicationContext).clearMemory()
                            Glide.with(activity)
                                    .load(image)
                                    .into(userImageViewProfilePicture)
                        }
                    }
                }
            }
            else -> {
                Toast.makeText(context,"Unrecognized request code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun copyInputStreamToFile(input: InputStream, mimeType : String) : File {
        val file = File(activity!!.cacheDir, "${System.currentTimeMillis()/1000}.$mimeType")
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

    fun getMimeType(context: Context, uri: Uri): String? {
        val extension: String?
        //Check uri format to avoid null
        if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            val mime = MimeTypeMap.getSingleton()
            extension = mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
        return extension
    }

}
