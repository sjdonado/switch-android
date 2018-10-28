package com.example.juan.aswitch.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
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
import com.example.juan.aswitch.activities.LoginActivity
import com.example.juan.aswitch.helpers.HttpClient
import org.json.JSONObject
import java.io.*
import android.webkit.MimeTypeMap
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.example.juan.aswitch.activities.MenuActivity
import com.example.juan.aswitch.helpers.Functions
import kotlin.math.sign


class UsersFragment : Fragment() {

    lateinit var mAuth: FirebaseAuth

    companion object {
        fun getInstance(): UsersFragment = UsersFragment()
        private val PICK_IMAGE = 0
    }

    private var signUp = false

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

        if (signUp) homeButtonAction.text = "Next" else homeButtonAction.text = "Save"

        homeImageViewProfilePicture.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }

        homeButtonAction.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("name", homeEditTextName.text)
            HttpClient.put("/users", jsonObject) { response ->
                Log.i("RESPONSE", response)
            }
            if(signUp) {
                requireActivity().startActivity(Intent(activity!!, MenuActivity::class.java))
            }else{
                Functions.showSnackbar(getView()!!, "Info updated!")
            }
        }

//        homeButtonLogout.setOnClickListener {
//            mAuth.signOut()
//            val intent = Intent(activity!!, LoginActivity::class.java)
//            requireActivity().startActivity(intent)
//        }
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
                    HttpClient.uploadImage("/users/upload", "profile_picture", image) { response ->
                        Log.i("RESPONSE", response.toString())
                    }
                    Glide.with(this)
                            .load(image)
                            .apply(RequestOptions.circleCropTransform())
                            .into(homeImageViewProfilePicture)
                }
            }
            else -> {
                Toast.makeText(context,"Unrecognized request code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun copyInputStreamToFile(input: InputStream, mimeType : String) : File {
        val file = File(activity!!.cacheDir, "file_tmp.${mimeType}")
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
