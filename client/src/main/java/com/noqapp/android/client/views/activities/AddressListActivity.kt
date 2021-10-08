package com.noqapp.android.client.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ActivityAddressbookBinding
import com.noqapp.android.client.model.api.ClientProfileApiImpl
import com.noqapp.android.client.presenter.ProfileAddressPresenter
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.utils.ShowAlertInformation
import com.noqapp.android.client.utils.UserUtils
import com.noqapp.android.client.views.adapters.AddressListAdapter
import com.noqapp.android.common.beans.JsonUserAddress
import com.noqapp.android.common.beans.JsonUserAddressList

/**
 * This class is used to list down all the addresses added by user
 * Created by Vivek Jha on 21/03/2021
 */
class AddressListActivity : BaseActivity(), ProfileAddressPresenter {
    private lateinit var activityAddressBookBinding: ActivityAddressbookBinding
    private lateinit var addressAdapter: AddressListAdapter
    private lateinit var clientProfileApiImpl: ClientProfileApiImpl
    private var primaryJsonUserAddress: JsonUserAddress? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddressBookBinding = ActivityAddressbookBinding.inflate(LayoutInflater.from(this))
        setContentView(activityAddressBookBinding.root)

        activityAddressBookBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        activityAddressBookBinding.btnAddAddress.setOnClickListener {
            val addAddressIntent = Intent(this, AddAddressActivity::class.java)
            startActivityForResult(addAddressIntent, Constants.REQUEST_CODE_ADD_ADDRESS)
        }

        clientProfileApiImpl = ClientProfileApiImpl()
        clientProfileApiImpl.setProfileAddressPresenter(this)

        clientProfileApiImpl.getProfileAllAddress(UserUtils.getEmail(), UserUtils.getAuth())

        setUpRecyclerView()
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

            R.id.rbAddress -> {
                AppInitialize.setSelectedAddressId(jsonUserAddress.id)
                val intent = Intent()
                intent.putExtra(Constants.JSON_USER_ADDRESS, jsonUserAddress)
                setResult(Constants.REQUEST_CODE_SELECT_ADDRESS, intent)
                finish()
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
            clientProfileApiImpl.deleteProfileAddress(UserUtils.getEmail(), UserUtils.getAuth(), jsonUserAddress)
        } else {
            ShowAlertInformation.showNetworkDialog(this)
        }
    }

    private fun setPrimaryAddress(jsonUserAddress: JsonUserAddress) {
        if (isOnline) {
            showProgress()
            setProgressMessage("Updating address...")
            clientProfileApiImpl.setPrimaryAddress(UserUtils.getEmail(), UserUtils.getAuth(), jsonUserAddress)
        } else {
            ShowAlertInformation.showNetworkDialog(this)
        }
    }

    override fun profileAddressResponse(jsonUserAddressList: JsonUserAddressList) {
        dismissProgress()

        val addressList = jsonUserAddressList.jsonUserAddresses
        showAddresses(addressList)
    }

    private fun showAddresses(addressList: List<JsonUserAddress>?) {
        val jp = AppInitialize.getUserProfile()
        jp.jsonUserAddresses = addressList
        AppInitialize.setUserProfile(jp)

        addressList?.forEach {
            if (it.isPrimaryAddress){
                primaryJsonUserAddress = it
                AppInitialize.setAddress(it.address)
                return@forEach
            }
        }

        addressList?.let {
            addressAdapter.addItems(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_ADD_ADDRESS){
            val jsonUserAddressList = data?.getSerializableExtra(Constants.ADDRESS_LIST)
            if (jsonUserAddressList is JsonUserAddressList){
                jsonUserAddressList.jsonUserAddresses?.let {
                    showAddresses(it)
                }
            }
        }
    }
}
