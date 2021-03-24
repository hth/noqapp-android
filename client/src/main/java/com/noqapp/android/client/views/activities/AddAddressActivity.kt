package com.noqapp.android.client.views.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ActivityAddAddressBinding
import com.noqapp.android.client.model.ClientProfileApiCall
import com.noqapp.android.client.presenter.ProfileAddressPresenter
import com.noqapp.android.client.utils.UserUtils
import com.noqapp.android.common.beans.JsonUserAddress
import com.noqapp.android.common.beans.JsonUserAddressList

class AddAddressActivity : LocationBaseActivity(), OnMapReadyCallback, ProfileAddressPresenter {

    private lateinit var addAddressBinding: ActivityAddAddressBinding
    private var googleMap: GoogleMap? = null
    private lateinit var clientProfileApiCall: ClientProfileApiCall
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var area: String = ""

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
            addAddress()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun addAddress() {
        val address = addAddressBinding.etHouseBuilding.text.toString() +
                ", " + addAddressBinding.etAddressLine1.text.toString() +
                ", " + addAddressBinding.etAddressLine2.text.toString()

        val jsonUserAddress = JsonUserAddress()
                .setAddress(address)
                .setCountryShortName("")
                .setArea(area)
                .setTown("")
                .setDistrict("")
                .setState("")
                .setStateShortName("")
                .setLatitude(latitude.toString())
                .setLongitude(longitude.toString())

        showProgress()
        clientProfileApiCall.addProfileAddress(UserUtils.getEmail(), UserUtils.getAuth(), jsonUserAddress)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        googleMap?.isMyLocationEnabled = true
    }

    override fun displayAddressOutput(addressOutput: String?, city: String?, latitude: Double?, longitude: Double?) {
        googleMap?.apply {
            latitude?.let { lat ->
                longitude?.let { lng ->
                    city?.let {
                        area = it
                    }
                    val currentLatLng = LatLng(lat, lng)
                    addAddressBinding.tvLocality.text = city
                    addAddressBinding.tvSublocality.text = addressOutput
                    this.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(currentLatLng, 16.0f)))
                }
            }
        }
    }

    override fun profileAddressResponse(jsonUserAddressList: JsonUserAddressList?) {
        dismissProgress()
        finish()
    }
}
