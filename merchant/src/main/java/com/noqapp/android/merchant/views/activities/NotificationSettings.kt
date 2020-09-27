package com.noqapp.android.merchant.views.activities

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
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
        setScreenOrientation()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)
        initActionsViews(false)
        tv_toolbar_title.text = "Notification Settings"
        val clientPreferenceApiCalls = MerchantPreferenceApiCalls()
        clientPreferenceApiCalls.setMerchantPreferencePresenter(this@NotificationSettings)
        setProgressMessage("Updating settings...")
        val sc_sound: SwitchCompat = findViewById(R.id.sc_sound)
        val sc_sms: SwitchCompat = findViewById(R.id.sc_sms)
        val sc_msg_announce: SwitchCompat = findViewById(R.id.sc_msg_announce)
        val cv_msg_announce: CardView = findViewById(R.id.cv_msg_announce)
        val sc_tv_split_view: SwitchCompat = findViewById(R.id.sc_tv_split_view)
        val cv_tv_split_view: CardView = findViewById(R.id.cv_tv_split_view)
        val cv_tv_refresh_time: CardView = findViewById(R.id.cv_tv_refresh_time)

        val rg_time: RadioGroup = findViewById(R.id.rg_time)
        val rb_3: RadioButton = findViewById(R.id.rb_3)
        val rb_5: RadioButton = findViewById(R.id.rb_5)
        val rb_7: RadioButton = findViewById(R.id.rb_7)
        val rb_10: RadioButton = findViewById(R.id.rb_10)

        val jsonUserPreference: JsonUserPreference? = BaseLaunchActivity.getLaunchActivity().getUserProfile().jsonUserPreference
        if (null == jsonUserPreference) {
            sc_sms.isChecked = AppInitialize.isNotificationReceiveEnable()
            sc_sound.isChecked = AppInitialize.isNotificationSoundEnable()
        } else {
            sc_sms.isChecked = jsonUserPreference.promotionalSMS == CommunicationModeEnum.R
            sc_sound.isChecked = jsonUserPreference.firebaseNotification == CommunicationModeEnum.R
        }
        if (packageName.equals("com.noqapp.android.merchant.tv", ignoreCase = true)) {
            sc_msg_announce.isChecked = LaunchActivity.isMsgAnnouncementEnable()
            sc_tv_split_view.isChecked = LaunchActivity.isTvSplitViewEnable()
            cv_msg_announce.visibility = View.VISIBLE
            cv_tv_split_view.visibility = View.VISIBLE
            cv_tv_refresh_time.visibility = View.VISIBLE

            when(LaunchActivity.getTvRefreshTime()) {
                3 -> {rb_3.isChecked = true}
                5-> {rb_5.isChecked = true}
                7 -> {rb_7.isChecked = true}
                10 -> {rb_10.isChecked = true}
            }
            sc_msg_announce.setOnCheckedChangeListener { buttonView, isChecked ->
                LaunchActivity.setMsgAnnouncementEnable(isChecked)
                if (isChecked) {
                    // The switch is enabled/checked
                    CustomToast().showToast(this@NotificationSettings, "Message Announcement Enabled")
                } else {
                    // The switch is disabled
                    CustomToast().showToast(this@NotificationSettings, "Message Announcement Disabled")
                }
            }
            sc_tv_split_view.setOnCheckedChangeListener { buttonView, isChecked ->
                LaunchActivity.setTvSplitViewEnable(isChecked)
                if (isChecked) {
                    // The switch is enabled/checked
                    CustomToast().showToast(this@NotificationSettings, "TV Split View Enabled")
                } else {
                    // The switch is disabled
                    CustomToast().showToast(this@NotificationSettings, "TV Split View Disabled")
                }
            }

            rg_time.setOnCheckedChangeListener { group, checkedId ->
                val rb = findViewById<View>(checkedId) as RadioButton
                when(rb.id) {
                    R.id.rb_3 -> {LaunchActivity.setTvRefreshTime(3)}
                    R.id.rb_5 -> {LaunchActivity.setTvRefreshTime(5)}
                    R.id.rb_7 -> {LaunchActivity.setTvRefreshTime(7)}
                    R.id.rb_10 -> {LaunchActivity.setTvRefreshTime(10)}
                }
                CustomToast().showToast(this@NotificationSettings, "TV refresh time set to : "+rb.text)
            }
        }else{
            cv_msg_announce.visibility = View.GONE
            cv_tv_split_view.visibility = View.GONE
            cv_tv_refresh_time.visibility = View.GONE
        }
        sc_sms.setOnCheckedChangeListener { buttonView, isChecked ->
            AppInitialize.setNotificationReceiveEnable(isChecked)
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
            AppInitialize.setNotificationSoundEnable(isChecked)
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
