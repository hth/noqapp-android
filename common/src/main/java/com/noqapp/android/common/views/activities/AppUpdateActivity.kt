package com.noqapp.android.common.views.activities

import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.noqapp.android.common.R
import com.noqapp.android.common.utils.CommonHelper

class AppUpdateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.act_app_update)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        val btn_download = findViewById<Button>(R.id.btn_download)
        btn_download.setOnClickListener {
            CommonHelper.openPlayStore(this@AppUpdateActivity)
            finish()
        }
    }

    override fun onBackPressed() {

    }
}
