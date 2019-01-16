package com.example.juan.aswitch.helpers

import android.transition.Fade
import android.transition.TransitionInflater
import android.transition.TransitionSet
import android.util.Log
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.juan.aswitch.R
import com.example.juan.aswitch.fragments.BaseFragment


class FragmentHandler(private val activity: AppCompatActivity, private val fragmentContainer: Int? = null) {

    companion object {
        private const val MOVE_DEFAULT_TIME: Long = 1000
        private const val FADE_DEFAULT_TIME: Long = 150
    }

    val currentFragment: BaseFragment?
        @Nullable
        get() {
            if (activity.supportFragmentManager.backStackEntryCount == 0) {
                return null
            }
            val currentEntry =
                    activity.supportFragmentManager
                            .getBackStackEntryAt(activity.supportFragmentManager.backStackEntryCount - 1)

            if (!currentEntry.name.isNullOrBlank()) {
                val tag = currentEntry.name
                Log.d("FRAGMENT_NAME", "" + tag)
                val fragment = activity.supportFragmentManager.findFragmentByTag(tag)
                Log.d("FRAGMENT", fragment.toString())
                return fragment as BaseFragment
            }
            return null
        }

    fun add(fragment: androidx.fragment.app.Fragment) {

        val enterTransitionSet = TransitionSet()
        enterTransitionSet.addTransition(TransitionInflater.from(activity).inflateTransition(android.R.transition.move))
        enterTransitionSet.duration = MOVE_DEFAULT_TIME
        enterTransitionSet.startDelay = FADE_DEFAULT_TIME
        fragment.sharedElementEnterTransition = enterTransitionSet

        val enterFade = Fade()
        enterFade.startDelay = FADE_DEFAULT_TIME
        enterFade.duration = FADE_DEFAULT_TIME
        fragment.enterTransition = enterFade

        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(fragmentContainer!!, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun add(fragment: BaseFragment) {
        //don't add a fragment of the same type on top of itself.
        val currentFragment = currentFragment
        if (currentFragment != null) {
            if (currentFragment::class === fragment::class) {
                Log.w("Fragment Manager", "Tried to add a fragment of the same type to the backstack. This may be done on purpose in some circumstances but generally should be avoided.")
                return
            }
        }

        val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
        Log.d("SET_FRAGMENT_NAME", fragment.getTitle())
        fragmentTransaction.replace(R.id.menu_fragment_container, fragment, fragment.getTitle())
        fragmentTransaction.addToBackStack(fragment.getTitle())
        fragmentTransaction.commit()
    }
}