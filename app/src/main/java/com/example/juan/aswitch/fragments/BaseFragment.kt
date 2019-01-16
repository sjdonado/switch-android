package com.example.juan.aswitch.fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.juan.aswitch.helpers.FragmentHandler


abstract class BaseFragment : androidx.fragment.app.Fragment() {

    private lateinit  var fragmentHandler: FragmentHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentHandler = FragmentHandler(activity!! as AppCompatActivity)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        activity!!.title = getTitle()
    }

    protected fun add(fragment: BaseFragment) {
        fragmentHandler.add(fragment)
    }

    abstract fun getTitle(): String
}