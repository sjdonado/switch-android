package com.example.juan.aswitch.activities

import com.example.juan.aswitch.R
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.example.juan.aswitch.fragments.HomeFragment
import com.example.juan.aswitch.fragments.UserFragment
import com.example.juan.aswitch.helpers.Functions
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_dashboard -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_home -> {
                val homeFragment = HomeFragment.getInstance()
                Functions.openFragment(this, R.id.menu_fragment_container, homeFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_users -> {
                val userFragment = UserFragment.getInstance()
                Functions.openFragment(this, R.id.menu_fragment_container, userFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
    }

}
