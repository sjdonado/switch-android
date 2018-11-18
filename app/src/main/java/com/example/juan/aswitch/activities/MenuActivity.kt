package com.example.juan.aswitch.activities

import android.content.Intent
import com.example.juan.aswitch.R
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.juan.aswitch.fragments.HomeFragment
import com.example.juan.aswitch.fragments.UserFragment
import com.example.juan.aswitch.helpers.Functions
import com.google.android.gms.location.places.ui.PlacePicker
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.fragment_users.*
import kotlinx.android.synthetic.main.navigation_header.*
import org.json.JSONObject

class MenuActivity : AppCompatActivity() {


    private lateinit var actionBar : ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Configure action bar
        setSupportActionBar(menu_toolbar)
        actionBar = supportActionBar!!
        actionBar.title = getString(R.string.title_activity_home)
        Functions.openFragment(this, R.id.menu_fragment_container, HomeFragment.getInstance())

        // Initialize the action bar drawer toggle instance
        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
                this,
                drawer_layout,
                menu_toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ){
            override fun onDrawerClosed(view: View){
                super.onDrawerClosed(view)
                //toast("Drawer closed")
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                val userObjectValue = Functions.getSharedPreferencesStringValue(this@MenuActivity, "USER_OBJECT")
                if(userObjectValue != null) {
                    val userObject = JSONObject(userObjectValue)
                    if(!userObject.isNull("profilePicture")) {
                        Glide.with(this@MenuActivity)
                                .load(userObject.getJSONObject("profilePicture").getString("url"))
                                .into(navigation_account_header_current)
                    }
                    if(!userObject.isNull("name")) navigation_account_header_name.text = userObject.getString("name")
                    if(!userObject.isNull("email")) navigation_account_header_email.text = userObject.getString("email")
                }

                navigation_account_header.setOnClickListener {
                    val userFragment = UserFragment.getInstance()
                    actionBar.title = getString(R.string.title_activity_user)
                    Functions.openFragment(this@MenuActivity, R.id.menu_fragment_container, userFragment)
                    drawer_layout.closeDrawer(GravityCompat.START)
                }
            }
        }

        // Configure the drawer layout to add listener and show icon on toolbar
        drawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        navigation.setNavigationItemSelectedListener {
            when (it.itemId){
//                R.id.navigation_dashboard -> {}
                R.id.navigation_home -> {
                    actionBar.title = getString(R.string.title_activity_home)
                    val homeFragment = HomeFragment.getInstance()
                    Functions.openFragment(this, R.id.menu_fragment_container, homeFragment)
                }
//                R.id.navigation_users -> {
//                    openUserFragment()
//                }
            }
            // Close the drawer
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }
    }

}
