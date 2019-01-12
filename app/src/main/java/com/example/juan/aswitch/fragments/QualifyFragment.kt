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
import com.example.juan.aswitch.models.Place
import com.example.juan.aswitch.models.Rate
import com.example.juan.aswitch.services.UsersPlaceService


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

        if (place.qualify != null) qualifyRatingBar.rating = place.qualify!!.toFloat()

        qualifySendButton.setOnClickListener {
            usersPlaceService.qualify(place.userPlaceId!!, qualifyRatingBar.rating) { res ->
                activity!!.runOnUiThread {
                    Log.d("QUALIFY_RESPONSE", res.getJSONObject("data").toString())
                    place.qualify = res.getJSONObject("data").getDouble("qualify")
                    val rate = res.getJSONObject("data").getJSONObject("rate")
                    place.rate = Rate(rate.getDouble("qualify"), rate.getInt("size"))
                    dismiss()
                }
            }
        }

        qualifyCancelButton.setOnClickListener {
            dismiss()
        }
    }


}
