package com.noqapp.android.client.views.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ActivityAddAddressBinding
import com.noqapp.android.client.model.api.ClientProfileApiCall
import com.noqapp.android.client.presenter.ProfileAddressPresenter
import com.noqapp.android.client.utils.AnimationUtil
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.utils.UserUtils
import com.noqapp.android.common.beans.JsonUserAddress
import com.noqapp.android.common.beans.JsonUserAddressList

/**
 * This class is used to add address
 * Created by Vivek Jha on 21/03/2021
 */
class AddAddressActivity : LocationBaseActivity(), OnMapReadyCallback, ProfileAddressPresenter {

    private lateinit var addAddressBinding: ActivityAddAddressBinding
    private var googleMap: GoogleMap? = null
    private lateinit var clientProfileApiCall: ClientProfileApiCall
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var countryShortName: String = ""
    private var area: String = ""
    private var town: String = ""
    private var district: String = ""
    private var state: String = ""
    private var stateShortName: String = ""
    private var isCameraMoving = false
    private var wasCameraChangeManual = false
    private var isAnimatingCamera = false
    private val queryHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addAddressBinding = ActivityAddAddressBinding.inflate(LayoutInflater.from(this))
        setContentView(addAddressBinding.root)
        setSupportActionBar(addAddressBinding.toolbar)

        addAddressBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        clientProfileApiCall = ClientProfileApiCall()
        clientProfileApiCall.setProfileAddressPresenter(this)

        addAddressBinding.btnAddAddress.setOnClickListener {
            if (validData())
                addAddress()
        }

        addAddressBinding.btnAllowLocationAccess.setOnClickListener {
            requestPermissions()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun validData(): Boolean {

        addAddressBinding.etName.let {
            if (it.text.toString().isEmpty()) {
                showSnackbar(R.string.txt_please_enter_full_name)
                return false
            }
        }

        addAddressBinding.etHouseBuilding.let {
            if (it.text.toString().isEmpty()) {
                showSnackbar(R.string.txt_please_enter_house_number)
                return false
            }
        }

        addAddressBinding.etAddress.let {
            if (it.text.toString().isEmpty()) {
                showSnackbar(R.string.txt_please_enter_address)
                return false
            }
        }

        return true
    }

    private fun addAddress() {
        val address =
            addAddressBinding.etName.text.toString() + ", " + addAddressBinding.etHouseBuilding.text.toString() + ", " + addAddressBinding.etAddress.text.toString()

        val jsonUserAddress = JsonUserAddress()
            .setCustomerName(addAddressBinding.etName.text.toString())
            .setAddress(address)
            .setCountryShortName(countryShortName)
            .setArea(area)
            .setTown(town)
            .setDistrict(district)
            .setState(state)
            .setStateShortName(stateShortName)
            .setLatitude(latitude.toString())
            .setLongitude(longitude.toString())

        showProgress()
        clientProfileApiCall.addProfileAddress(
            UserUtils.getEmail(),
            UserUtils.getAuth(),
            jsonUserAddress
        )
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        googleMap?.isMyLocationEnabled = true
        googleMap?.setOnCameraMoveStartedListener(onCameraMoveStartedListener)
        googleMap?.setOnCameraIdleListener(onCameraIdleListener)
    }

    override fun displayAddressOutput(
        addressOutput: String?,
        countryShortName: String?,
        area: String?,
        town: String?,
        district: String?,
        state: String?,
        stateShortName: String?,
        latitude: Double?,
        longitude: Double?
    ) {
        latitude?.let { lat ->
            longitude?.let { lng ->
                this@AddAddressActivity.latitude = lat
                this@AddAddressActivity.longitude = lng
                countryShortName?.let {
                    this.countryShortName = it
                }
                area?.let {
                    this.area = it
                }
                town?.let {
                    this.town = it
                }
                district?.let {
                    this.district = it
                }
                state?.let {
                    this.state = it
                }
                stateShortName?.let {
                    this.stateShortName = it
                }

                val currentLatLng = LatLng(lat, lng)
                var locationAsString = this.town
                if (!area.isNullOrBlank()) {
                    locationAsString = this.area + ", " + this.town
                }
                addAddressBinding.tvLocality.text = locationAsString
                addAddressBinding.etName.setText(AppInitialize.getUserProfile().name)

                addAddressBinding.etAddress.setText(addressOutput)
                animateCamera(
                    (CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(
                            currentLatLng,
                            16.0f
                        )
                    ))
                )
            }
        }
    }

    override fun locationPermissionRequired() {
        addAddressBinding.cvLocationAccessRequired.visibility = View.VISIBLE
    }

    override fun locationPermissionGranted() {
        addAddressBinding.cvLocationAccessRequired.visibility = View.GONE
    }

    override fun profileAddressResponse(jsonUserAddressList: JsonUserAddressList?) {
        val primaryAddress = jsonUserAddressList?.jsonUserAddresses?.last()
        primaryAddress?.let {
            AppInitialize.setAddress(it.address)
            AppInitialize.setSelectedAddressId(it.id)
        }

        val intent = Intent()
        intent.putExtra(Constants.ADDRESS_LIST, jsonUserAddressList)
        setResult(Activity.RESULT_OK, intent)

        dismissProgress()
        finish()
    }

    private val onCameraIdleListener = GoogleMap.OnCameraIdleListener {
        isCameraMoving = false
        if (wasCameraChangeManual) {
            queryHandler.postDelayed(queryRunnable, 200)
        } else {
            wasCameraChangeManual = true
        }
        AnimationUtil.stopMarkerAnimation(addAddressBinding.ivCenterMarker)
        addAddressBinding.viewShadow.visibility = View.GONE
    }

    private val onCameraMoveStartedListener = GoogleMap.OnCameraMoveStartedListener {
        wasCameraChangeManual = !isAnimatingCamera
        isCameraMoving = true
        queryHandler.removeCallbacks(queryRunnable)
        if (wasCameraChangeManual) {
            AnimationUtil.stopMarkerAnimation(addAddressBinding.ivCenterMarker)
            addAddressBinding.viewShadow.visibility = View.VISIBLE
        }
    }

    private val queryRunnable = Runnable {
        val cameraPosition = googleMap?.cameraPosition
        getMapLocation(cameraPosition?.target?.latitude, cameraPosition?.target?.longitude)
    }

    private fun animateCamera(update: CameraUpdate) {
        isAnimatingCamera = true
        googleMap?.animateCamera(update, 200, object : GoogleMap.CancelableCallback {
            override fun onFinish() {
                isAnimatingCamera = false
            }

            override fun onCancel() {
                isAnimatingCamera = false
            }
        })
    }
}
