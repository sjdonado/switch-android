package com.example.juan.aswitch.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.juan.aswitch.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_users.*
import android.util.Log
import com.bumptech.glide.Glide
import org.json.JSONObject
import java.io.*
import android.webkit.MimeTypeMap
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.example.juan.aswitch.activities.MenuActivity
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.services.UserService
import com.google.android.gms.location.places.ui.PlacePicker
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.example.juan.aswitch.models.User
import com.google.android.material.textfield.TextInputLayout


class UserFragment : androidx.fragment.app.Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var userService: UserService
    private var signUp: Boolean = false
    private var role: Boolean = false
    private val userJSONObject = JSONObject()
    lateinit var user: User

    companion object {
        fun getInstance(): UserFragment = UserFragment()
        private const val PICK_IMAGE = 0
        var PLACE_PICKER_REQUEST = 1
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

        user = Utils.getSharedPreferencesUserObject(activity!!)
        signUp = Utils.getSharedPreferencesBooleanValue(activity!!, "SIGN_UP")!!
        Log.d("USER", user.toString())

        Glide.with(activity!!)
                .load(user.profilePicture?.url)
                .apply(Utils.glideRequestOptions(activity!!))
                .into(userImageViewProfilePicture)

        userEditTextName.editText!!.setText(user.name)
        userEditTextEmail.editText!!.setText(user.email)

        userEditTextLocation.editText!!
                .setText(user.location?.address)

        if(user.role != null) {
            role = user.role!!
            userEditTextNit.visibility = View.VISIBLE
            userEditTextSignboard.visibility = View.VISIBLE
        }

        if(signUp) userSwitchAccountType.visibility = View.VISIBLE

        setEditTextValidation(userEditTextName, getString(R.string.users_fragment_name_user_error))
        setEditTextValidation(userEditTextEmail, getString(R.string.users_fragment_email_error))
        setEditTextValidation(userEditTextLocation, getString(R.string.users_fragment_location_error))
        setEditTextValidation(userEditTextNit, getString(R.string.users_fragment_nit_error))
        setEditTextValidation(userEditTextSignboard, getString(R.string.users_fragment_signboard_error))

        userEditTextLocation.editText!!.setOnClickListener {
            val builder = PlacePicker.IntentBuilder()
            val southwest = user.location?.viewport?.southwest
            val northeast = user.location?.viewport?.northeast
            if(southwest != null && northeast != null) {
                builder.setLatLngBounds(LatLngBounds(
                        LatLng(southwest.lat, southwest.lng),
                        LatLng(northeast.lat, northeast.lng))
                )
            }
            startActivityForResult(builder.build(activity!!), PLACE_PICKER_REQUEST)
        }

        if (signUp)
            userButtonAction.setImageResource(R.drawable.ic_arrow_forward_white_24dp)
        else
            userButtonAction.setImageResource(R.drawable.ic_save_white_24dp)

        userSwitchAccountType.setOnCheckedChangeListener { _, bChecked ->
            role = bChecked
            if (bChecked) {
                userSwitchAccountType.setText(R.string.users_fragment_account_type_company)
                userEditTextNit.visibility = View.VISIBLE
                userEditTextSignboard.visibility = View.VISIBLE
            } else {
                userSwitchAccountType.setText(R.string.users_fragment_account_type_user)
                userEditTextNit.visibility = View.INVISIBLE
                userEditTextSignboard.visibility = View.INVISIBLE
            }
        }

        userImageViewProfilePicture.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivityForResult(Intent.createChooser(intent,
                    getString(R.string.user_fragment_select_picture)), PICK_IMAGE)
        }

        userButtonAction.setOnClickListener {
            if(saveButtonValidation(userEditTextName, getString(R.string.users_fragment_name_user_error))
                && saveButtonValidation(userEditTextEmail, getString(R.string.users_fragment_email_error))
                && saveButtonValidation(userEditTextLocation, getString(R.string.users_fragment_location_error))
                && saveButtonValidation(userEditTextNit, getString(R.string.users_fragment_nit_error))
                && saveButtonValidation(userEditTextSignboard, getString(R.string.users_fragment_signboard_error))) {

                userJSONObject.put("name", userEditTextName.editText!!.text)
                userJSONObject.put("email", userEditTextEmail.editText!!.text)
                if(signUp) userJSONObject.put("role", role)
                if(userEditTextNit.visibility == View.VISIBLE)
                    userJSONObject.put("nit", userEditTextNit.editText!!.text)
                if(userEditTextSignboard.visibility == View.VISIBLE)
                    userJSONObject.put("signboard", userEditTextSignboard.editText!!.text)

                userService.update(userJSONObject) { res ->
                    Utils.showSnackbar(getView()!!, getString(R.string.alert_info_updated))
                    Utils.setSharedPreferencesBooleanValue(activity!!, "SIGN_UP", false)
                    Utils.updateSharedPreferencesObjectValue(activity!!, Utils.USER_OBJECT, res.getJSONObject("data"))
                }
                if(signUp){
                    val menuActivityIntent = Intent(activity!!, MenuActivity::class.java)
                    requireActivity().startActivity(menuActivityIntent)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            PICK_IMAGE -> {
                if(resultCode == Activity.RESULT_OK && data != null) {
                    Log.i("DATA_URI", data.data!!.path)
                    val image = copyInputStreamToFile(
                            activity!!.contentResolver!!.openInputStream(data.data!!)!!,
                            getMimeType(activity!!, data.data!!)!!)
                    userService.uploadImage("/upload", "profilePicture", image) { res ->
                        Utils.showSnackbar(view!!, getString(R.string.alert_profile_picture_updated))
                        activity!!.runOnUiThread {
                            Utils.updateSharedPreferencesObjectValue(
                                    activity!!,
                                    "USER_OBJECT",
                                    res.getJSONObject("data")
                            )
                            Glide.with(activity!!)
                                    .load(res.getJSONObject("data")
                                            .getJSONObject("profilePicture")
                                            .getString("url"))
                                    .apply(Utils.glideRequestOptions(activity!!))
                                    .into(userImageViewProfilePicture)
                        }
                    }
                }
            }
            PLACE_PICKER_REQUEST -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    val place = PlacePicker.getPlace(activity!!, data)
                    val locationObject = JSONObject()
                    val viewPort = JSONObject()
                    val southwest = JSONObject()
                    val northeast = JSONObject()
                    if(place.isDataValid) {
                        southwest.put("lat", place.viewport!!.southwest.latitude)
                        southwest.put("lng", place.viewport!!.southwest.longitude)

                        northeast.put("lat", place.viewport!!.northeast.latitude)
                        northeast.put("lng", place.viewport!!.northeast.longitude)

                        viewPort.put("southwest", southwest)
                        viewPort.put("northeast", northeast)

                        locationObject.put("lat", place.latLng.latitude)
                        locationObject.put("lng", place.latLng.longitude)
                        locationObject.put("viewport", viewPort)
                        locationObject.put("address", place.address)
                        userJSONObject.put("location", locationObject)
                        userEditTextLocation.editText!!.setText(place.address)
                    }
                }
            }
            else -> {
                Log.e("INTENT", "Unrecognized request code")
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
        if (uri.scheme!! == ContentResolver.SCHEME_CONTENT) {
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

    private fun saveButtonValidation(editTextLayout: TextInputLayout, errorValue: String): Boolean {
        var valid = true
        if(editTextLayout.visibility == View.VISIBLE && (editTextLayout.editText!!.text.isEmpty() || editTextLayout.editText!!.text.isNullOrBlank())){
            editTextLayout.error = errorValue
            valid = false
        } else editTextLayout.error = null
        return valid
    }

    private fun setEditTextValidation(editTextLayout: TextInputLayout, errorValue: String){
        editTextLayout.editText!!.afterTextChanged { validateEditText(editTextLayout, editTextLayout.editText!!, errorValue) }
        editTextLayout.editText!!.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateEditText(editTextLayout, editTextLayout.editText!!, errorValue)
            }
        }
    }

    private fun validateEditText(layout: TextInputLayout, editText: EditText, errorValue: String) {
        if (editText.text.isEmpty()) layout.error = errorValue else layout.error = null
    }

    private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }

}
