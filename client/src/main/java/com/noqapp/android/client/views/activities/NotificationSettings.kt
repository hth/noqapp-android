package com.noqapp.android.client.views.activities

import android.os.Bundle
import androidx.appcompat.widget.SwitchCompat
import com.noqapp.android.client.R
import com.noqapp.android.client.model.ClientPreferenceApiCalls
import com.noqapp.android.client.presenter.ClientPreferencePresenter
import com.noqapp.android.client.utils.UserUtils
import com.noqapp.android.common.beans.JsonProfile
import com.noqapp.android.common.beans.JsonUserPreference
import com.noqapp.android.common.customviews.CustomToast
import com.noqapp.android.common.model.types.CommunicationModeEnum


class NotificationSettings : BaseActivity(), ClientPreferencePresenter {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)
        initActionsViews(true)
        tv_toolbar_title.text = "Notification Settings"

        val clientPreferenceApiCalls: ClientPreferenceApiCalls = ClientPreferenceApiCalls()
        clientPreferenceApiCalls.setClientPreferencePresenter(this@NotificationSettings)
        setProgressMessage("Updating settings...")
        val sc_sound: SwitchCompat = findViewById(R.id.sc_sound)
        val sc_sms: SwitchCompat = findViewById(R.id.sc_sms)
        //  sc_sms.isChecked = MyApplication.isNotificationReceiveEnable()
        //  sc_sound.isChecked = MyApplication.isNotificationSoundEnable()
        val jsonUserPreference: JsonUserPreference = LaunchActivity.getUserProfile().jsonUserPreference
        sc_sms.isChecked = jsonUserPreference.promotionalSMS == CommunicationModeEnum.R
        sc_sound.isChecked = jsonUserPreference.firebaseNotification == CommunicationModeEnum.R
        sc_sms.setOnCheckedChangeListener { buttonView, isChecked ->
            MyApplication.setNotificationReceiveEnable(isChecked)
            if (isChecked) {
                // The switch is enabled/checked
                CustomToast().showToast(this@NotificationSettings, "SMS Enabled")
            } else {
                // The switch is disabled
                CustomToast().showToast(this@NotificationSettings, "SMS Disabled")
            }
            showProgress()
            clientPreferenceApiCalls.promotionalSMS(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth())
        }
        sc_sound.setOnCheckedChangeListener { _buttonView, isChecked ->
            MyApplication.setNotificationSoundEnable(isChecked)
            if (isChecked) {
                // The switch is enabled/checked
                CustomToast().showToast(this@NotificationSettings, "Sound Enabled")
            } else {
                // The switch is disabled
                CustomToast().showToast(this@NotificationSettings, "Sound Disabled")
            }
            showProgress()
            clientPreferenceApiCalls.notificationSound(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth())
        }

    }

    override fun clientPreferencePresenterResponse(jsonUserPreference: JsonUserPreference?) {
        dismissProgress()
        val jsonProfile: JsonProfile = LaunchActivity.getUserProfile()
        jsonProfile.setJsonUserPreference(jsonUserPreference);
        LaunchActivity.setUserProfile(jsonProfile)
        CustomToast().showToast(this@NotificationSettings, "Settings updated successfully")
    }

}
