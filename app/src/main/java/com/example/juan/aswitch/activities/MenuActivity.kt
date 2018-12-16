package com.example.juan.aswitch.activities

import com.example.juan.aswitch.R
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.preference.PreferenceFragment
import androidx.preference.PreferenceFragmentCompat
import com.bumptech.glide.Glide
import com.example.juan.aswitch.R.id.filter_action
import com.example.juan.aswitch.R.id.navigation_notifications
import com.example.juan.aswitch.fragments.*
import com.example.juan.aswitch.helpers.Functions
import com.example.juan.aswitch.services.PlaceService
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.fragment_users.*
import kotlinx.android.synthetic.main.navigation_header.*
import org.json.JSONObject


class MenuActivity : AppCompatActivity() {

    private lateinit var actionBar : ActionBar
    private var userObject: JSONObject = JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Configure action bar
        setSupportActionBar(menu_toolbar)
        actionBar = supportActionBar!!
        actionBar.title = getString(R.string.title_fragment_home)
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
                    userObject = JSONObject(userObjectValue)
                    if(!userObject.isNull("profilePicture")) {
                        Glide.with(this@MenuActivity)
                                .load(userObject.getJSONObject("profilePicture").getString("url"))
                                .apply(Functions.glideRequestOptions(this@MenuActivity))
                                .into(navigation_account_header_current)
                    }
                    if(!userObject.isNull("name")) navigation_account_header_name.text = userObject.getString("name")
                    if(!userObject.isNull("email")) navigation_account_header_email.text = userObject.getString("email")
                    if(!userObject.isNull("role") && userObject.getBoolean("role")) navigation.menu.findItem(navigation_notifications).isVisible = true
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
            when (it.itemId) {
//                R.id.navigation_dashboard -> {}
                R.id.navigation_home -> {
                    actionBar.title = getString(R.string.title_fragment_home)
                    val homeFragment = HomeFragment.getInstance()
                    Functions.openFragment(this, R.id.menu_fragment_container, homeFragment)
                }
                R.id.navigation_notifications -> {
                    actionBar.title = getString(R.string.title_fragment_notifications)
                    val notificationsFragment = NotificationsFragment.getInstance()
                    Functions.openFragment(this, R.id.menu_fragment_container, notificationsFragment)
                }
                R.id.navigation_settings -> {
                    actionBar.title = getString(R.string.title_fragment_settings)
                    if(!userObject.isNull("role") && userObject.getBoolean("role")){
                        Functions.openFragment(this, R.id.menu_fragment_container, CompanySettingsFragment.getInstance())
                    }else{
                        Functions.openFragment(this, R.id.menu_fragment_container, UserSettingsFragment.getInstance())
                    }
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
