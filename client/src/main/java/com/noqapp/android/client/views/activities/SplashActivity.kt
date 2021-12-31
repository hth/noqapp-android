package com.noqapp.android.client.views.activities

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.noqapp.android.client.R
import com.noqapp.android.client.views.version_2.HomeActivity

class SplashActivity : BaseActivity() {

    private val TAG = SplashActivity::class.java.simpleName


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        AppInitialize.setLocationChangedManually(false)
        val animationView = findViewById<LottieAnimationView>(R.id.animation_view)
        animationView.setAnimation("data.json")
        animationView.playAnimation()
//        animationView.repeatCount = 1
        animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                callLaunchScreen()
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }
        })
        val clAllowLocationPermission = findViewById<View>(R.id.cl_location_access_required)
        clAllowLocationPermission.visibility = View.GONE
    }


    private fun callLaunchScreen() {
        val i = Intent(this, HomeActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()
    }

}