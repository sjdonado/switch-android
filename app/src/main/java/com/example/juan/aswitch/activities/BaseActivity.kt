package com.example.juan.aswitch.activities

import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.view.GravityCompat
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.juan.aswitch.helpers.FragmentHandler
import com.example.juan.aswitch.lib.BackButtonSupportFragment


abstract class BaseActivity : AppCompatActivity() {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentHandler: FragmentHandler

    private val navigationBackPressListener = View.OnClickListener { fragmentManager.popBackStack() }
    private val backStackListener = FragmentManager.OnBackStackChangedListener { onBackStackChangedEvent() }

    private fun onBackStackChangedEvent() {
        syncDrawerToggleState()
    }

    protected fun syncDrawerToggleState() {
        if (fragmentManager.backStackEntryCount > 1 && fragmentHandler.currentFragment != null) {
            getDrawerToggle().isDrawerIndicatorEnabled = false
            getDrawerToggle().toolbarNavigationClickListener = navigationBackPressListener //pop backstack
        } else {
            getDrawerToggle().isDrawerIndicatorEnabled = true
            getDrawerToggle().toolbarNavigationClickListener = getDrawerToggle().toolbarNavigationClickListener //open nav menu drawer
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager = supportFragmentManager
        fragmentHandler = FragmentHandler(this)
        fragmentManager.addOnBackStackChangedListener(backStackListener)
    }

    override fun onDestroy() {
        fragmentManager.removeOnBackStackChangedListener(backStackListener)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (sendBackPressToDrawer()) {
            //the drawer consumed the backpress
            return
        }

        if (sendBackPressToFragmentOnTop()) {
            // fragment on top consumed the back press
            return
        }

        //let the android system handle the back press, usually by popping the fragment
        super.onBackPressed()

        //close the activity if back is pressed on the root fragment
        if (fragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    private fun sendBackPressToDrawer(): Boolean {
        if (getDrawer().isDrawerOpen(GravityCompat.START)) {
            getDrawer().closeDrawer(GravityCompat.START)
            return true
        }
        return false
    }

    private fun sendBackPressToFragmentOnTop(): Boolean {
        val fragmentOnTop = fragmentHandler.currentFragment ?: return false
        return if (fragmentOnTop !is BackButtonSupportFragment) {
            false
        } else (fragmentOnTop as BackButtonSupportFragment).onBackPressed()
    }

    protected abstract fun getDrawerToggle(): ActionBarDrawerToggle

    protected abstract fun getDrawer(): DrawerLayout

    protected abstract fun getFragmentContainerId(): Int

}