package com.noqapp.android.client.utils

import android.animation.ObjectAnimator
import android.view.View

class AnimationUtil {

    companion object {

        fun stopMarkerAnimation(view: View) {
            val animator = ObjectAnimator.ofFloat(view, "translationY", 0f)
            animator.start()
        }

        fun startMarkerAnimation(view: View) {
            val animator = ObjectAnimator.ofFloat(view, "translationY", -50f)
            animator.start()
        }

    }

}