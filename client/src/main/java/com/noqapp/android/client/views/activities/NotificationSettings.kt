package com.noqapp.android.client.views.activities

import android.os.Bundle
import androidx.appcompat.widget.SwitchCompat
import com.noqapp.android.client.R
import com.noqapp.android.common.customviews.CustomToast


class NotificationSettings : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)
        initActionsViews(true)
        tv_toolbar_title.text = "Notification Settings"

        var sc_sound: SwitchCompat = findViewById(R.id.sc_sound)
        var sc_sms: SwitchCompat = findViewById(R.id.sc_sms)
        sc_sms.isChecked = MyApplication.isNotificationReceiveEnable()
        sc_sound.isChecked = MyApplication.isNotificationSoundEnable()
        sc_sms.setOnCheckedChangeListener { buttonView, isChecked ->
            MyApplication.setNotificationReceiveEnable(isChecked)
            if (isChecked) {
                // The switch is enabled/checked
                CustomToast().showToast(this@NotificationSettings,"Sms Enable")
            } else {
                // The switch is disabled
                CustomToast().showToast(this@NotificationSettings,"Sms Disable")
            }
        }
        sc_sound.setOnCheckedChangeListener { buttonView, isChecked ->
            MyApplication.setNotificationSoundEnable(isChecked)
            if (isChecked) {
                // The switch is enabled/checked
                CustomToast().showToast(this@NotificationSettings,"Sound Enable")
            } else {
                // The switch is disabled
                CustomToast().showToast(this@NotificationSettings,"Sound Disable")
            }
        }

    }

}
