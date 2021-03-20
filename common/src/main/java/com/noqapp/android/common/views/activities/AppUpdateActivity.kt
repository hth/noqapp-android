package com.noqapp.android.common.views.activities

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.noqapp.android.common.R
import com.noqapp.android.common.utils.CommonHelper

class AppUpdateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.act_app_update)
        val btn_download = findViewById<Button>(R.id.btn_download)
        btn_download.setOnClickListener {
            CommonHelper.openPlayStore(this@AppUpdateActivity)
            finish()
        }
    }

    override fun onBackPressed() {

    }
}
