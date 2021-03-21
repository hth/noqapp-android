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

class AddAddressActivity : LocationBaseActivity(), OnMapReadyCallback {

    private lateinit var addAddressBinding: ActivityAddAddressBinding
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addAddressBinding = ActivityAddAddressBinding.inflate(LayoutInflater.from(this))
        setContentView(addAddressBinding.root)
        setSupportActionBar(addAddressBinding.toolbar)

        addAddressBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
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
                    val currentLatLng = LatLng(lat, lng)
                    addAddressBinding.tvLocality.text = city
                    addAddressBinding.tvSublocality.text = addressOutput
                    this.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(currentLatLng, 16.0f)))
                }
            }
        }
    }
}
