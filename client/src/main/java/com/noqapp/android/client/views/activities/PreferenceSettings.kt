package com.noqapp.android.client.views.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.noqapp.android.client.R
import com.noqapp.android.client.model.api.ClientPreferenceApiCalls
import com.noqapp.android.client.presenter.ClientPreferencePresenter
import com.noqapp.android.client.utils.UserUtils
import com.noqapp.android.common.beans.JsonProfile
import com.noqapp.android.common.beans.JsonUserPreference
import com.noqapp.android.common.customviews.CustomToast
import com.noqapp.android.common.model.types.CommunicationModeEnum
import com.noqapp.android.common.model.types.order.DeliveryModeEnum
import com.noqapp.android.common.model.types.order.PaymentMethodEnum

class PreferenceSettings : BaseActivity(), ClientPreferencePresenter {

    override fun onCreate(savedInstanceState: Bundle?) {
        hideSoftKeys(AppInitialize.isLockMode)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)
        initActionsViews(true)
        tv_toolbar_title.text = getString(R.string.preference_settings)
        val clientPreferenceApiCalls = ClientPreferenceApiCalls()
        clientPreferenceApiCalls.setClientPreferencePresenter(this@PreferenceSettings)
        setProgressMessage("Updating settings...")
        var isCash = false
        var isHomeDelivery = false
        val cv_address: CardView = findViewById(R.id.cv_address)
        val sc_sound: SwitchCompat = findViewById(R.id.sc_sound)
        val sc_sms: SwitchCompat = findViewById(R.id.sc_sms)
        val sc_msg_announce: SwitchCompat = findViewById(R.id.sc_msg_announce)
        val tv_home_delivery: TextView = findViewById(R.id.tv_home_delivery)
        val tv_take_away: TextView = findViewById(R.id.tv_take_away)
        val tv_cash: TextView = findViewById(R.id.tv_cash)
        val tv_online: TextView = findViewById(R.id.tv_online)
        val btn_update: Button = findViewById(R.id.btn_update)
        tv_home_delivery.setOnClickListener {
            isHomeDelivery = true
            tv_take_away.setBackgroundResource(R.drawable.square_white_bg_drawable)
            tv_home_delivery.setBackgroundColor(ContextCompat.getColor(this@PreferenceSettings, R.color.review_color))
            tv_home_delivery.setTextColor(Color.WHITE)
            tv_take_away.setTextColor(Color.BLACK)
        }
        tv_take_away.setOnClickListener {
            isHomeDelivery = false
            tv_home_delivery.setBackgroundResource(R.drawable.square_white_bg_drawable)
            tv_take_away.setBackgroundColor(ContextCompat.getColor(this@PreferenceSettings, R.color.review_color))
            tv_take_away.setTextColor(Color.WHITE)
            tv_home_delivery.setTextColor(Color.BLACK)
        }
        tv_cash.setOnClickListener {
            isCash = true;
            tv_online.setBackgroundResource(R.drawable.square_white_bg_drawable)
            tv_cash.setBackgroundColor(ContextCompat.getColor(this@PreferenceSettings, R.color.review_color))
            tv_cash.setTextColor(Color.WHITE)
            tv_online.setTextColor(Color.BLACK)
        }
        tv_online.setOnClickListener {
            isCash = false;
            tv_cash.setBackgroundResource(R.drawable.square_white_bg_drawable)
            tv_online.setBackgroundColor(ContextCompat.getColor(this@PreferenceSettings, R.color.review_color))
            tv_online.setTextColor(Color.WHITE)
            tv_cash.setTextColor(Color.BLACK)
        }
        cv_address.setOnClickListener {
            val intent = Intent(this@PreferenceSettings, AddressListActivity::class.java)
            startActivity(intent)
        }
        btn_update.setOnClickListener {
            showProgress()
            val jsonUserPreference: JsonUserPreference = AppInitialize.getUserProfile().jsonUserPreference
            if (isHomeDelivery) {
                jsonUserPreference.deliveryMode = DeliveryModeEnum.HD
            } else {
                jsonUserPreference.deliveryMode = DeliveryModeEnum.TO
            }
            if (isCash) {
                jsonUserPreference.paymentMethod = PaymentMethodEnum.CA
            } else {
                jsonUserPreference.paymentMethod = PaymentMethodEnum.EL
            }
            clientPreferenceApiCalls.order(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonUserPreference)
        }
        sc_msg_announce.isChecked = AppInitialize.isMsgAnnouncementEnable()

        var jsonUserPreference: JsonUserPreference? = null
        if (null != AppInitialize.getUserProfile() && null != AppInitialize.getUserProfile().jsonUserPreference) {
            jsonUserPreference = AppInitialize.getUserProfile().jsonUserPreference
        }
        if (null == jsonUserPreference) {
            sc_sms.isChecked = AppInitialize.isNotificationReceiveEnable()
            sc_sound.isChecked = AppInitialize.isNotificationSoundEnable()
        } else {
            sc_sms.isChecked = jsonUserPreference.promotionalSMS == CommunicationModeEnum.R
            sc_sound.isChecked = jsonUserPreference.firebaseNotification == CommunicationModeEnum.R
        }
        if (null != jsonUserPreference && jsonUserPreference.deliveryMode == DeliveryModeEnum.HD) {
            tv_home_delivery.performClick()
        } else {
            tv_take_away.performClick()
        }

        if (null != jsonUserPreference && jsonUserPreference.paymentMethod == PaymentMethodEnum.CA) {
            tv_cash.performClick()
        } else {
            tv_online.performClick()
        }
        sc_sms.setOnCheckedChangeListener { _, isChecked ->
            AppInitialize.setNotificationReceiveEnable(isChecked)
            if (isChecked) {
                // The switch is enabled/checked
                CustomToast().showToast(this@PreferenceSettings, "Promotional SMS Enabled")
            } else {
                // The switch is disabled
                CustomToast().showToast(this@PreferenceSettings, "Promotional SMS Disabled")
            }
            // showProgress()
            clientPreferenceApiCalls.promotionalSMS(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth())
        }

        sc_sound.setOnCheckedChangeListener { _, isChecked ->
            AppInitialize.setNotificationSoundEnable(isChecked)
            if (isChecked) {
                // The switch is enabled/checked
                CustomToast().showToast(this@PreferenceSettings, "Notification Sound Enabled")
            } else {
                // The switch is disabled
                CustomToast().showToast(this@PreferenceSettings, "Notification Sound Disabled")
            }
            //showProgress()
            clientPreferenceApiCalls.notificationSound(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth())
        }

        sc_msg_announce.setOnCheckedChangeListener { _, isChecked ->
            AppInitialize.setMsgAnnouncementEnable(isChecked)
            if (isChecked) {
                // The switch is enabled/checked
                CustomToast().showToast(this@PreferenceSettings, "Message Announcement Enabled")
            } else {
                // The switch is disabled
                CustomToast().showToast(this@PreferenceSettings, "Message Announcement Disabled")
            }
        }
    }

    override fun clientPreferencePresenterResponse(jsonUserPreference: JsonUserPreference?) {
        dismissProgress()
        val jsonProfile: JsonProfile = AppInitialize.getUserProfile()
        jsonProfile.jsonUserPreference = jsonUserPreference
        AppInitialize.setUserProfile(jsonProfile)
        CustomToast().showToast(this@PreferenceSettings, "Settings updated successfully")
    }
}
