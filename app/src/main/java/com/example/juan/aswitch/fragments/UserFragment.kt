package com.example.juan.aswitch.fragments


import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
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
import android.text.format.DateFormat
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import com.example.juan.aswitch.models.Place
import com.example.juan.aswitch.models.Time
import com.example.juan.aswitch.models.User
import com.example.juan.aswitch.services.PlaceService
import com.google.android.material.textfield.TextInputLayout
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class UserFragment : androidx.fragment.app.Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var userService: UserService
    private lateinit var placeService: PlaceService
    private lateinit var user: User
    private lateinit var place: Place
    private var signUp: Boolean = false
    private var role: Boolean = false
    private val userJSONObject = JSONObject()
    private lateinit var openingTime: Time
    private lateinit var closingTime: Time
    private lateinit var progressDialog: Dialog

    companion object {
        fun getInstance(): UserFragment = UserFragment()
        private const val PICK_IMAGE = 0
        private const val PLACE_PICKER_REQUEST = 1
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
        placeService = PlaceService(activity!!)

        user = Utils.getSharedPreferencesUserObject(activity!!)
        signUp = Utils.getSharedPreferencesBooleanValue(activity!!, "SIGN_UP")!!

        Log.d("USER", user.toString())

        Glide.with(activity!!)
                .load(user.profilePicture?.url)
                .apply(Utils.glideRequestOptions(activity!!))
                .into(userProfilePictureImageView)

        userNameEditText.editText!!.setText(user.name)
        userEmailEditText.editText!!.setText(user.email)

        userLocationEditText.editText!!.setText(user.location?.address)

        if(user.role != null) role = user.role!!

        if (role) toggleCompanyFieldsVisibility(true)

        if(signUp || (user.role != null && role)) {
            placeService.getAllCategories {
                activity!!.runOnUiThread {
                    Log.d("getCategories", it.toString())
                    val categories = it.getJSONArray("data")
                    val spinnerArrayAdapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item,
                            Utils.toStringArray(categories)!!)
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                            .simple_spinner_dropdown_item)
                    userCategorySpinner.adapter = spinnerArrayAdapter
                }
            }
        }

        if(user.role != null && role) {
            placeService.get {
                activity!!.runOnUiThread {
                    place = Utils.parseJSONPlace(it.getJSONObject("data"))
                    userNitEditText.editText!!.setText(place.nit)
                    userSignboardEditText.editText!!.setText(place.signboard)
                    userDescriptionEditText.editText!!.setText(place.description)
                    setTimePicker(activity!!, userOpeningTimeEditText.editText!!, 0, place.openingTime)
                    setTimePicker(activity!!, userClosingTimeEditText.editText!!, 1, place.closingTime)
                }
            }
        }

        if(signUp && user.role == null) {
            userSwitchAccountType.visibility = View.VISIBLE
            setTimePicker(activity!!, userOpeningTimeEditText.editText!!, 0, null)
            setTimePicker(activity!!, userClosingTimeEditText.editText!!, 1, null)
        }

        setEditTextValidation(userNameEditText, getString(R.string.users_fragment_name_user_error))
        setEditTextValidation(userEmailEditText, getString(R.string.users_fragment_email_error))
        setEditTextValidation(userLocationEditText, getString(R.string.users_fragment_location_error))
        setEditTextValidation(userNitEditText, getString(R.string.users_fragment_nit_error))
        setEditTextValidation(userSignboardEditText, getString(R.string.users_fragment_signboard_error))
        setEditTextValidation(userDescriptionEditText, getString(R.string.users_fragment_description_error))
        setEditTextValidation(userOpeningTimeEditText, getString(R.string.users_fragment_opening_time_error))
        setEditTextValidation(userClosingTimeEditText, getString(R.string.users_fragment_closing_time_error))

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

        userLocationEditText.editText!!.setOnClickListener {
            userLocationEditText.isEnabled = false
            progressDialog = Utils.showLoading(activity!!)
            val builder = PlacePicker.IntentBuilder()
            val southwest = user.location?.viewport?.southwest
            val northeast = user.location?.viewport?.northeast
            if(southwest != null && northeast != null) {
                builder.setLatLngBounds(LatLngBounds(
                        LatLng(southwest.lat, southwest.lng),
                        LatLng(northeast.lat, northeast.lng))
                )
            }
            progressDialog.hide()
            startActivityForResult(builder.build(activity!!), PLACE_PICKER_REQUEST)
        }

        userButtonAction.setOnClickListener {
            if(saveButtonValidation(userNameEditText, getString(R.string.users_fragment_name_user_error))
                && saveButtonValidation(userEmailEditText, getString(R.string.users_fragment_email_error))
                && saveButtonValidation(userLocationEditText, getString(R.string.users_fragment_location_error))
                && saveButtonValidation(userNitEditText, getString(R.string.users_fragment_nit_error))
                && saveButtonValidation(userSignboardEditText, getString(R.string.users_fragment_signboard_error))
                && saveButtonValidation(userOpeningTimeEditText, getString(R.string.users_fragment_opening_time_error))
                && saveButtonValidation(userClosingTimeEditText, getString(R.string.users_fragment_closing_time_error))) {

                userJSONObject.put("name", userNameEditText.editText!!.text)
                userJSONObject.put("email", userEmailEditText.editText!!.text)
                if(signUp) userJSONObject.put("role", role)
                if(role) {
                    userJSONObject.put("nit", userNitEditText.editText!!.text)
                    userJSONObject.put("signboard", userSignboardEditText.editText!!.text)
                    userJSONObject.put("description", userDescriptionEditText.editText!!.text)
                    userJSONObject.put("category", userCategorySpinner.selectedItem.toString())
                    val openingTimeJsonObject = JSONObject()
                    openingTimeJsonObject.put("hourOfDay", openingTime.hourOfDay)
                    openingTimeJsonObject.put("minute", openingTime.minute)
                    userJSONObject.put("openingTime", openingTimeJsonObject)
                    val closingTimeJsonObject = JSONObject()
                    closingTimeJsonObject.put("hourOfDay", closingTime.hourOfDay)
                    closingTimeJsonObject.put("minute", openingTime.minute)
                    userJSONObject.put("closingTime", closingTimeJsonObject)
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
                    userLocationEditText.isEnabled = true
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
            userOpeningTimeEditText.visibility = View.VISIBLE
            userClosingTimeEditText.visibility = View.VISIBLE
            userCategoryInputLabel.visibility = View.VISIBLE
        } else {
            userCategoryInputLabel.visibility = View.GONE
            userNitEditText.visibility = View.GONE
            userSignboardEditText.visibility = View.GONE
            userDescriptionEditText.visibility = View.GONE
            userCategorySpinner.visibility = View.GONE
            userOpeningTimeEditText.visibility = View.GONE
            userClosingTimeEditText.visibility = View.GONE
        }
    }

//    private fun setDatePicker(editText: EditText, activity: Activity) {
//        editText.setText(SimpleDateFormat("dd/MM/yyyy", Locale.US).format(System.currentTimeMillis()))
//        val cal = Calendar.getInstance()
//
//        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
//            cal.set(Calendar.YEAR, year)
//            cal.set(Calendar.MONTH, monthOfYear)
//            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//
//            val myFormat = "dd/MM/yyyy" // mention the format you need
//            val sdf = SimpleDateFormat(myFormat, Locale.US)
//            editText.setText(sdf.format(cal.time))
//        }
//
//        editText.setOnClickListener {
//            DatePickerDialog(activity, dateSetListener,
//                    cal.get(Calendar.YEAR),
//                    cal.get(Calendar.MONTH),
//                    cal.get(Calendar.DAY_OF_MONTH)).show()
//        }
//    }

    private fun setTimePicker(activity: Activity, editText: EditText, ref: Int, default: Time?) {
//        val is24HoursFormat = DateFormat.is24HourFormat(activity)
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)

        val cal = Calendar.getInstance()

        if(default !== null) {
            cal.set(Calendar.HOUR_OF_DAY, default.hourOfDay!!)
            cal.set(Calendar.MINUTE, default.minute!!)
            editText.setText(simpleDateFormat.format(cal.time))
            setStringDate(Time(default.hourOfDay, default.minute), ref)
        }

        val timePickerDialog = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            setStringDate(Time(hourOfDay, minute), ref)
            editText.setText(simpleDateFormat.format(cal.time))
        }

        editText.setOnClickListener {
            TimePickerDialog(activity, timePickerDialog,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true).show()
        }
    }

    private fun setStringDate(date: Time, ref: Int) {
        when(ref) {
            0 -> openingTime = date
            1 -> closingTime = date
        }
    }

}
