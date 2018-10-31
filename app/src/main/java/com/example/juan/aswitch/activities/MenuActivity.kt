package com.example.juan.aswitch.activities

import com.example.juan.aswitch.R
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.juan.aswitch.fragments.HomeFragment
import com.example.juan.aswitch.fragments.UserFragment
import com.example.juan.aswitch.helpers.Functions
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
        actionBar.title = "Inicio"

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
                val userObjectValue = Functions.getSharedPreferencesValue(this@MenuActivity, "USER_OBJECT")
                if(userObjectValue != null) {
                    val userObject = JSONObject(userObjectValue)
                    if(!userObject.getString("profile_picture").isNullOrEmpty()) {
                        Glide.with(this@MenuActivity)
                                .load(userObject.getString("profile_picture"))
                                .into(navigation_account_header_current)
                    }
                    if(!userObject.getString("name").isNullOrEmpty()) navigation_account_header_name.text = userObject.getString("name")
                    if(!userObject.getString("email").isNullOrEmpty()) navigation_account_header_email.text = userObject.getString("email")
                }

                navigation_account_header.setOnClickListener {
                    val userFragment = UserFragment.getInstance()
                    actionBar.title = "User"
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
                    actionBar.title = "Home"
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
