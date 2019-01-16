package com.example.juan.aswitch.activities

import com.example.juan.aswitch.R
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.juan.aswitch.R.id.navigationNotifications
import com.example.juan.aswitch.R.id.navigationStarredPlaces
import com.example.juan.aswitch.fragments.*
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.models.User
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.navigation_header.*
import androidx.drawerlayout.widget.DrawerLayout
import com.example.juan.aswitch.helpers.FragmentHandler


class MenuActivity : BaseActivity() {

    private lateinit var actionBar : ActionBar
    private lateinit var user: User
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentHandler: FragmentHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        fragmentManager = supportFragmentManager
        fragmentHandler = FragmentHandler(this@MenuActivity, R.id.menu_fragment_container)

        // Configure action bar
        setSupportActionBar(menu_toolbar)
        actionBar = supportActionBar!!

        actionBar.setDisplayHomeAsUpEnabled(true)

        actionBar.title = getString(R.string.title_fragment_home)
        user = Utils.getSharedPreferencesUserObject(this@MenuActivity)
        openHomeFragment()

        // Initialize the action bar drawer toggle instance
        drawerToggle = object : ActionBarDrawerToggle(
                this,
                drawer_layout,
                menu_toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ){
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)

                Glide.with(this@MenuActivity)
                        .load(user.profilePicture?.url)
                        .apply(Utils.glideRequestOptions(this@MenuActivity))
                        .into(navigationHeaderProfilePicture)
                navigationHeaderName.text = user.name
                navigationHeaderEmail.text = user.email

                if(user.role!!) {
                    navigation.menu.findItem(navigationNotifications).isVisible = true
                } else {
                    navigation.menu.findItem(navigationStarredPlaces).isVisible = true
                }

                navigation_account_header.setOnClickListener {
                    val userFragment = UserFragment.getInstance()
                    actionBar.title = getString(R.string.title_activity_user)
                    fragmentHandler.add(userFragment)
                    drawer_layout.closeDrawer(GravityCompat.START)
                }
            }
        }

        // Configure the drawer layout to add listener and show icon on toolbar
        drawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        syncDrawerToggleState()

        navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
//                R.id.navigation_dashboard -> {}
                R.id.navigationHome -> {
                    openHomeFragment()
                }
                R.id.navigationStarredPlaces -> {
                    actionBar.title = getString(R.string.title_fragment_starred_places)
                    fragmentHandler.add(StarredPlacesFragment.getInstance())
                }
                R.id.navigationNotifications -> {
                    actionBar.title = getString(R.string.title_fragment_notifications)
                    val notificationsFragment = NotificationsFragment.getInstance()
                    fragmentHandler.add(notificationsFragment)
                }
                R.id.navigationSettings -> {
                    actionBar.title = getString(R.string.title_fragment_settings)
                    fragmentHandler.add(SettingsFragment.getInstance())
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

    private fun openHomeFragment() {
        actionBar.title = getString(R.string.title_fragment_home)
        if(user.role!!) {
            fragmentHandler.add(PlaceFragment.getInstance())
        } else {
            fragmentHandler.add(SwipeFragment.getInstance())
        }
    }


    private val navigationBackPressListener = View.OnClickListener { fragmentManager.popBackStack() }

    override fun getDrawerToggle(): ActionBarDrawerToggle {
        return drawerToggle
    }

    override fun getDrawer(): DrawerLayout {
        return drawer_layout
    }

    override fun getFragmentContainerId(): Int {
        return R.id.menu_fragment_container
    }

}
