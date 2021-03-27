package com.noqapp.android.client.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ActivityAddressbookBinding
import com.noqapp.android.client.model.ClientPreferenceApiCalls
import com.noqapp.android.client.model.ClientProfileApiCall
import com.noqapp.android.client.presenter.ClientPreferencePresenter
import com.noqapp.android.client.presenter.ProfileAddressPresenter
import com.noqapp.android.client.utils.ShowAlertInformation
import com.noqapp.android.client.utils.UserUtils
import com.noqapp.android.client.views.adapters.AddressListAdapter
import com.noqapp.android.common.beans.JsonUserAddress
import com.noqapp.android.common.beans.JsonUserAddressList
import com.noqapp.android.common.beans.JsonUserPreference

class AddressListActivity : BaseActivity(), ProfileAddressPresenter {

    private lateinit var activityAddressBookBinding: ActivityAddressbookBinding
    private lateinit var addressAdapter: AddressListAdapter
    private lateinit var clientProfileApiCall: ClientProfileApiCall

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddressBookBinding = ActivityAddressbookBinding.inflate(LayoutInflater.from(this))
        setContentView(activityAddressBookBinding.root)

        activityAddressBookBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        activityAddressBookBinding.btnAddAddress.setOnClickListener {
            val `in` = Intent(this, AddAddressActivity::class.java)
            startActivityForResult(`in`, 78)
        }

        clientProfileApiCall = ClientProfileApiCall()
        clientProfileApiCall.setProfileAddressPresenter(this)

        setUpRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        clientProfileApiCall.getProfileAllAddress(UserUtils.getEmail(), UserUtils.getAuth())
    }

    private fun setUpRecyclerView() {
        addressAdapter = AddressListAdapter(mutableListOf()) { jsonUserAddress, view ->
            handleClickListener(jsonUserAddress, view)
        }

        activityAddressBookBinding.rvAddress.layoutManager = LinearLayoutManager(this)
        activityAddressBookBinding.rvAddress.adapter = addressAdapter
    }

    private fun handleClickListener(jsonUserAddress: JsonUserAddress, view: View) {
        when (view.id) {
            R.id.ivDelete -> {
                removeAddress(jsonUserAddress)
            }

            else -> {
                setPrimaryAddress(jsonUserAddress)
            }
        }
    }

    private fun removeAddress(jsonUserAddress: JsonUserAddress) {
        if (isOnline) {
            showProgress()
            setProgressMessage("Deleting address...")
            clientProfileApiCall.deleteProfileAddress(UserUtils.getEmail(), UserUtils.getAuth(), jsonUserAddress)
        } else {
            ShowAlertInformation.showNetworkDialog(this)
        }
    }

    private fun setPrimaryAddress(jsonUserAddress: JsonUserAddress) {
        if (isOnline) {
            showProgress()
            setProgressMessage("Updating address...")

            clientProfileApiCall.setPrimaryAddress(UserUtils.getEmail(), UserUtils.getAuth(), jsonUserAddress)
        } else {
            ShowAlertInformation.showNetworkDialog(this)
        }
    }

    override fun profileAddressResponse(jsonUserAddressList: JsonUserAddressList) {
        dismissProgress()
        val addressList = jsonUserAddressList.jsonUserAddresses
        val jp = AppInitialize.getUserProfile()
        jp.jsonUserAddresses = addressList
        AppInitialize.setUserProfile(jp)
        addressAdapter.addItems(addressList)
        dismissProgress()
    }

}
