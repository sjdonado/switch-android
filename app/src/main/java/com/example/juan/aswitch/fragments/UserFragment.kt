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
import com.example.juan.aswitch.activities.MenuActivity
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.services.UserService
import com.google.android.gms.location.places.ui.PlacePicker
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import com.example.juan.aswitch.models.Place
import com.example.juan.aswitch.models.User
import com.example.juan.aswitch.services.PlaceService
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.render_type_text.view.*


class UserFragment : androidx.fragment.app.Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var userService: UserService
    private lateinit var placeService: PlaceService
    private lateinit var user: User
    private lateinit var place: Place
    private var signUp: Boolean = false
    private var role: Boolean = false
    private val userJSONObject = JSONObject()
    private lateinit var categoryAdapter: ArrayAdapter<CharSequence>


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
                .into(userProfilePictureImageView)

        userNameEditText.editText!!.setText(user.name)
        userEmailEditText.editText!!.setText(user.email)

        userLocationEditText.editText!!
                .setText(user.location?.address)

        categoryAdapter = ArrayAdapter.createFromResource(
                activity!!,
                R.array.place_spinner_values,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            userCategorySpinner.adapter = adapter
        }

        if(user.role != null) role = user.role!!

        if (role) toggleCompanyFieldsVisibility(true)

        if(user.role != null && role) {
            placeService = PlaceService(activity!!)
            placeService.get {
                activity!!.runOnUiThread {
                    place = Utils.parseJSONPlace(it.getJSONObject("data"))
                    userNitEditText.editText!!.setText(place.nit)
                    userSignboardEditText.editText!!.setText(place.signboard)
                    userDescriptionEditText.editText!!.setText(place.description)
                    userCategorySpinner.setSelection(categoryAdapter.getPosition(place.category))
                }
            }
        }

        if(signUp && user.role == null) userSwitchAccountType.visibility = View.VISIBLE

        setEditTextValidation(userNameEditText, getString(R.string.users_fragment_name_user_error))
        setEditTextValidation(userEmailEditText, getString(R.string.users_fragment_email_error))
        setEditTextValidation(userLocationEditText, getString(R.string.users_fragment_location_error))
        setEditTextValidation(userNitEditText, getString(R.string.users_fragment_nit_error))
        setEditTextValidation(userSignboardEditText, getString(R.string.users_fragment_signboard_error))
        setEditTextValidation(userDescriptionEditText, getString(R.string.users_fragment_description_error))

        userLocationEditText.editText!!.setOnClickListener {
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
                toggleCompanyFieldsVisibility(true)
            } else {
                userSwitchAccountType.setText(R.string.users_fragment_account_type_user)
                toggleCompanyFieldsVisibility(false)
            }
        }

        userProfilePictureImageView.setOnClickListener {
            Utils.openImagePickerIntent(this, PICK_IMAGE)
        }

        userButtonAction.setOnClickListener {
            if(saveButtonValidation(userNameEditText, getString(R.string.users_fragment_name_user_error))
                && saveButtonValidation(userEmailEditText, getString(R.string.users_fragment_email_error))
                && saveButtonValidation(userLocationEditText, getString(R.string.users_fragment_location_error))
                && saveButtonValidation(userNitEditText, getString(R.string.users_fragment_nit_error))
                && saveButtonValidation(userSignboardEditText, getString(R.string.users_fragment_signboard_error))
                && saveButtonValidation(userDescriptionEditText, getString(R.string.users_fragment_description_error))) {

                userJSONObject.put("name", userNameEditText.editText!!.text)
                userJSONObject.put("email", userEmailEditText.editText!!.text)
                if(signUp) userJSONObject.put("role", role)
                if(role) {
                    userJSONObject.put("nit", userNitEditText.editText!!.text)
                    userJSONObject.put("signboard", userSignboardEditText.editText!!.text)
                    userJSONObject.put("description", userDescriptionEditText.editText!!.text)
                    userJSONObject.put("category", userCategorySpinner.selectedItem.toString())
                }
                Log.d("INFO_TO_UPDATE", userJSONObject.toString())
                userService.update(userJSONObject) { res ->
                    Utils.showSnackbar(getView()!!, getString(R.string.alert_info_updated))
                    Utils.setSharedPreferencesBooleanValue(activity!!, "SIGN_UP", false)
                    Utils.updateSharedPreferencesObjectValue(activity!!, Utils.USER_OBJECT, res.getJSONObject("data"))
                    activity!!.runOnUiThread {
                        if(signUp){
                            val menuActivityIntent = Intent(activity!!, MenuActivity::class.java)
                            requireActivity().startActivity(menuActivityIntent)
                        }
                    }
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
                    val image = Utils.copyInputStreamToFile(
                            activity!!,
                            activity!!.contentResolver!!.openInputStream(data.data!!)!!,
                            Utils.getMimeType(activity!!, data.data!!)!!
                    )
                    userService.uploadImage("profilePicture", image) { res ->
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
                                    .into(userProfilePictureImageView)
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
                        userLocationEditText.editText!!.setText(place.address)
                    }
                }
            }
            else -> {
                Log.e("INTENT", "Unrecognized request code")
            }
        }
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

    private fun toggleCompanyFieldsVisibility(visible: Boolean) {
        if (visible) {
            userNitEditText.visibility = View.VISIBLE
            userSignboardEditText.visibility = View.VISIBLE
            userDescriptionEditText.visibility = View.VISIBLE
            userCategorySpinner.visibility = View.VISIBLE
        } else {
            userNitEditText.visibility = View.INVISIBLE
            userSignboardEditText.visibility = View.INVISIBLE
            userDescriptionEditText.visibility = View.INVISIBLE
            userCategorySpinner.visibility = View.INVISIBLE
        }
    }

}
