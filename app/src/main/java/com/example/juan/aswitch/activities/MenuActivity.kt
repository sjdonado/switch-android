package com.example.juan.aswitch.activities

import com.example.juan.aswitch.R
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.juan.aswitch.R.id.navigationNotifications
import com.example.juan.aswitch.R.id.navigationStarredPlaces
import com.example.juan.aswitch.fragments.*
import com.example.juan.aswitch.helpers.Utils
import kotlinx.android.synthetic.main.activity_menu.*
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
        Utils.openFragment(this, R.id.menu_fragment_container, HomeFragment.getInstance())

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
                val userObjectValue = Utils.getSharedPreferencesStringValue(this@MenuActivity, "USER_OBJECT")
                if(userObjectValue != null) {
                    userObject = JSONObject(userObjectValue)
                    if(!userObject.isNull("profilePicture")) {
                        Glide.with(this@MenuActivity)
                                .load(userObject.getJSONObject("profilePicture").getString("url"))
                                .apply(Utils.glideRequestOptions(this@MenuActivity))
                                .into(navigation_account_header_current)
                    }
                    if(!userObject.isNull("name")) navigation_account_header_name.text = userObject.getString("name")
                    if(!userObject.isNull("email")) navigation_account_header_email.text = userObject.getString("email")
                    if(!userObject.isNull("role")) {
                        if(userObject.getBoolean("role")) {
                            navigation.menu.findItem(navigationNotifications).isVisible = true
                        } else {
                            navigation.menu.findItem(navigationStarredPlaces).isVisible = true
                        }
                    }
                }

                navigation_account_header.setOnClickListener {
                    val userFragment = UserFragment.getInstance()
                    actionBar.title = getString(R.string.title_activity_user)
                    Utils.openFragment(this@MenuActivity, R.id.menu_fragment_container, userFragment)
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
                R.id.navigationHome -> {
                    actionBar.title = getString(R.string.title_fragment_home)
                    val homeFragment = HomeFragment.getInstance()
                    Utils.openFragment(this, R.id.menu_fragment_container, homeFragment)
                }
                R.id.navigationStarredPlaces -> {
                    actionBar.title = getString(R.string.title_fragment_starred_places)
                    Utils.openFragment(this, R.id.menu_fragment_container, StarredPlacesFragment.getInstance())
                }
                R.id.navigationNotifications -> {
                    actionBar.title = getString(R.string.title_fragment_notifications)
                    val notificationsFragment = NotificationsFragment.getInstance()
                    Utils.openFragment(this, R.id.menu_fragment_container, notificationsFragment)
                }
                R.id.navigationSettings -> {
                    actionBar.title = getString(R.string.title_fragment_settings)
                    Utils.openFragment(this, R.id.menu_fragment_container, SettingsFragment.getInstance())
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
