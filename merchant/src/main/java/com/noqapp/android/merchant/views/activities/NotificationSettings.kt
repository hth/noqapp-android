package com.noqapp.android.merchant.views.activities

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.noqapp.android.common.beans.JsonProfile
import com.noqapp.android.common.beans.JsonUserPreference
import com.noqapp.android.common.customviews.CustomToast
import com.noqapp.android.common.model.types.CommunicationModeEnum
import com.noqapp.android.merchant.R
import com.noqapp.android.merchant.model.MerchantPreferenceApiCalls
import com.noqapp.android.merchant.utils.UserUtils
import com.noqapp.android.merchant.views.interfaces.MerchantPreferencePresenter

class NotificationSettings : BaseActivity(), MerchantPreferencePresenter {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)
        val fl_notification = findViewById<FrameLayout>(R.id.fl_notification)
        val tv_toolbar_title = findViewById<TextView>(R.id.tv_toolbar_title)
        val actionbarBack = findViewById<ImageView>(R.id.actionbarBack)
        fl_notification.visibility = View.INVISIBLE
        actionbarBack.setOnClickListener { v -> finish() }
        tv_toolbar_title.text = "Notification Settings"
        val clientPreferenceApiCalls = MerchantPreferenceApiCalls()
        clientPreferenceApiCalls.setMerchantPreferencePresenter(this@NotificationSettings)
        setProgressMessage("Updating settings...")
        val sc_sound: SwitchCompat = findViewById(R.id.sc_sound)
        val sc_sms: SwitchCompat = findViewById(R.id.sc_sms)

        val jsonUserPreference: JsonUserPreference? = BaseLaunchActivity.getLaunchActivity().getUserProfile().jsonUserPreference
        if (null == jsonUserPreference) {
            sc_sms.isChecked = MyApplication.isNotificationReceiveEnable()
            sc_sound.isChecked = MyApplication.isNotificationSoundEnable()
        } else {
            sc_sms.isChecked = jsonUserPreference.promotionalSMS == CommunicationModeEnum.R
            sc_sound.isChecked = jsonUserPreference.firebaseNotification == CommunicationModeEnum.R
        }

        sc_sms.setOnCheckedChangeListener { buttonView, isChecked ->
            MyApplication.setNotificationReceiveEnable(isChecked)
            if (isChecked) {
                // The switch is enabled/checked
                CustomToast().showToast(this@NotificationSettings, "Promotional SMS Enabled")
            } else {
                // The switch is disabled
                CustomToast().showToast(this@NotificationSettings, "Promotional SMS Disabled")
            }
            // showProgress()
            clientPreferenceApiCalls.promotionalSMS(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth())
        }

        sc_sound.setOnCheckedChangeListener { _buttonView, isChecked ->
            MyApplication.setNotificationSoundEnable(isChecked)
            if (isChecked) {
                // The switch is enabled/checked
                CustomToast().showToast(this@NotificationSettings, "Notification Sound Enabled")
            } else {
                // The switch is disabled
                CustomToast().showToast(this@NotificationSettings, "Notification Sound Disabled")
            }
            //showProgress()
            clientPreferenceApiCalls.notificationSound(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth())
        }
    }

    override fun merchantPreferencePresenterResponse(jsonUserPreference: JsonUserPreference?) {
        //dismissProgress()
        val jsonProfile: JsonProfile = BaseLaunchActivity.getLaunchActivity().getUserProfile()
        jsonProfile.jsonUserPreference = jsonUserPreference
        BaseLaunchActivity.getLaunchActivity().setUserProfile(jsonProfile)
        CustomToast().showToast(this@NotificationSettings, "Settings updated successfully")
    }
}
