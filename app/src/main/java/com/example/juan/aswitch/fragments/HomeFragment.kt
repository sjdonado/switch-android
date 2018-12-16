package com.example.juan.aswitch.fragments


import android.os.Bundle
import android.view.*

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.Functions
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity


class HomeFragment : androidx.fragment.app.Fragment() {

    companion object {
        fun getInstance(): HomeFragment = HomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.filter_action -> {
                Functions.openFragment(activity as AppCompatActivity, R.id.menu_fragment_container, FiltersFragment.getInstance())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
