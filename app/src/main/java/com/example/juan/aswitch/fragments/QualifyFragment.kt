package com.example.juan.aswitch.fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_qualify.*


import com.example.juan.aswitch.R
import com.example.juan.aswitch.models.MyQualify
import com.example.juan.aswitch.models.Place
import com.example.juan.aswitch.models.Rate
import com.example.juan.aswitch.services.UsersPlaceService
import com.google.gson.Gson


class QualifyFragment : DialogFragment() {

    private lateinit var place: Place
    private lateinit var usersPlaceService: UsersPlaceService

    companion object {
        fun getInstance(place: Place) = QualifyFragment().apply {
            arguments = Bundle().apply {
                putParcelable("PLACE", place)
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getParcelable<Place>("PLACE")?.let {
            place = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qualify, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usersPlaceService = UsersPlaceService(activity!!)

        if (place.myQualify != null) {
            qualifyRatingBar.rating = place.myQualify!!.value.toFloat()
            qualifyCommentTextField.editText!!.setText(place.myQualify!!.comment)
        }

        qualifySendButton.setOnClickListener {
            usersPlaceService.qualify(place.userPlaceId!!, qualifyRatingBar.rating, qualifyCommentTextField.editText!!.text.toString()) { res ->
                activity!!.runOnUiThread {
                    place.myQualify = Gson().fromJson(res.getJSONObject("data").getJSONObject("myQualify").toString(), MyQualify::class.java)
                    place.rate = Gson().fromJson(res.getJSONObject("data").getJSONObject("rate").toString(), Rate::class.java)
                    dismiss()
                }
            }
        }

        qualifyCancelButton.setOnClickListener {
            dismiss()
        }
    }


}
