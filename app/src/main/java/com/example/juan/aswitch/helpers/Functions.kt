package com.example.juan.aswitch.helpers

import com.example.juan.aswitch.R
import android.support.design.widget.Snackbar
import android.transition.Fade
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionInflater
import android.transition.TransitionSet
import android.view.View

open class Functions {

    companion object {

        private const val MOVE_DEFAULT_TIME: Long = 1000
        private const val FADE_DEFAULT_TIME: Long = 300

        fun showSnackbar (rootLayout : View, text : String ) {
            Snackbar.make(
                    rootLayout,
                    text,
                    Snackbar.LENGTH_SHORT
            ).show()
        }

        fun openFragment(activity: AppCompatActivity, fragment_id : Int, fragment: Fragment) {
//            val exitFade = Fade()
//            exitFade.setDuration(FADE_DEFAULT_TIME)
//            previousFragment.setExitTransition(exitFade)
//
//            val enterTransitionSet = TransitionSet()
//            enterTransitionSet.addTransition(TransitionInflater.from(activity).inflateTransition(android.R.transition.move))
//            enterTransitionSet.setDuration(MOVE_DEFAULT_TIME)
//            enterTransitionSet.setStartDelay(FADE_DEFAULT_TIME)
//            fragment.setSharedElementEnterTransition(enterTransitionSet)
//
//            val enterFade = Fade()
//            enterFade.startDelay = FADE_DEFAULT_TIME
//            enterFade.duration = FADE_DEFAULT_TIME
//            fragment.enterTransition = enterFade

            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.replace(fragment_id, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}

