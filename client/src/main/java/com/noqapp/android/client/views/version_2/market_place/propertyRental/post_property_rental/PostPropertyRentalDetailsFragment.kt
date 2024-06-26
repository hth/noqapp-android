package com.noqapp.android.client.views.version_2.market_place.propertyRental.post_property_rental

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.FragmentPostPropertyRentalDetailsBinding
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.market_place.propertyRental.PropertyRentalViewModel
import com.noqapp.android.common.model.types.category.RentalTypeEnum
import com.noqapp.android.common.pojos.PropertyRentalEntity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.views.activities.AddAddressActivity
import com.noqapp.android.common.beans.JsonUserAddress
import com.noqapp.android.common.utils.CommonHelper
import java.util.*

class PostPropertyRentalDetailsFragment : BaseFragment(), OnDateSetListener, OnMapReadyCallback {

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val jsonUserAddress = intent?.getSerializableExtra(Constants.JSON_USER_ADDRESS) as JsonUserAddress
                this.jsonUserAddress = jsonUserAddress
                if (jsonUserAddress.address != null && jsonUserAddress.address != "") {
                    fragmentPostPropertyRentalDetails.tvAddress.text = jsonUserAddress.address
                }
                fragmentPostPropertyRentalDetails.etCityArea.setText(jsonUserAddress.area.toString())
                fragmentPostPropertyRentalDetails.etTownLocality.setText(jsonUserAddress.town.toString())

                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            jsonUserAddress.latitude.toDouble(),
                            jsonUserAddress.longitude.toDouble()
                        ), 16.0f
                    )
                )

            }
        }

    private lateinit var fragmentPostPropertyRentalDetails: FragmentPostPropertyRentalDetailsBinding
    private lateinit var propertyRentalViewModel: PropertyRentalViewModel
    private lateinit var postPropertyRentalDetailsFragmentInteractionListener: PostPropertyRentalDetailsFragmentInteractionListener
    private var jsonUserAddress: JsonUserAddress? = null
    private var propertyRentalEntityVal: PropertyRentalEntity? = null
    private lateinit var googleMap: GoogleMap

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PostPropertyRentalDetailsFragmentInteractionListener)
            postPropertyRentalDetailsFragmentInteractionListener = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPostPropertyRentalDetails = FragmentPostPropertyRentalDetailsBinding.inflate(inflater, container, false)
        propertyRentalViewModel = ViewModelProvider(requireActivity())[PropertyRentalViewModel::class.java]
        return fragmentPostPropertyRentalDetails.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }
        observeData()
        setListeners()
        val spAdapter = ArrayAdapter(requireActivity(),R.layout.spinner_item, RentalTypeEnum.asListOfDescription())
        fragmentPostPropertyRentalDetails.spinnerRentalType.adapter = spAdapter
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun observeData() {
        propertyRentalViewModel.getPropertyRental(requireContext())
            .observe(viewLifecycleOwner, {
                if (it.isNotEmpty()) {
                    val propertyRentalEntity = it[0]
                    propertyRentalEntityVal = propertyRentalEntity

                    if (propertyRentalEntity.address != null && propertyRentalEntity.address != "")
                        fragmentPostPropertyRentalDetails.tvAddress.text = propertyRentalEntity.address

                    if (propertyRentalEntity.coordinates[0] != 0.0) {
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    propertyRentalEntity.coordinates[0],
                                    propertyRentalEntity.coordinates[1]
                                ), 16.0f
                            )
                        )
                    }

                    fragmentPostPropertyRentalDetails.etLandmark.setText(propertyRentalEntity.landmark)
                    fragmentPostPropertyRentalDetails.etTownLocality.setText(propertyRentalEntity.town)
                    fragmentPostPropertyRentalDetails.etCityArea.setText(propertyRentalEntity.city)
                    fragmentPostPropertyRentalDetails.etCarpetArea.setText(propertyRentalEntity.carpetArea.toString())
                    fragmentPostPropertyRentalDetails.etPrice.setText(propertyRentalEntity.price.toString())
                    fragmentPostPropertyRentalDetails.tvAvailableFrom.text = propertyRentalEntity.availableFrom
                    fragmentPostPropertyRentalDetails.spinnerRentalType.setSelection(RentalTypeEnum.asListOfDescription().indexOf(propertyRentalEntity.rentalType))

                    selectBedroom(propertyRentalEntity.bedroom)
                    selectBathRoom(propertyRentalEntity.bathroom)
                }
            })
    }

    private fun selectBedroom(bedRoom: Int) {
        fragmentPostPropertyRentalDetails.tv1Bhk.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)
        fragmentPostPropertyRentalDetails.tv2Bhk.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)
        fragmentPostPropertyRentalDetails.tv3Bhk.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)
        fragmentPostPropertyRentalDetails.tv4Bhk.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)
        fragmentPostPropertyRentalDetails.tv5Bhk.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)

        when (bedRoom) {
            1 -> {
                fragmentPostPropertyRentalDetails.tv1Bhk.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bedroom = 1
            }
            2 -> {
                fragmentPostPropertyRentalDetails.tv2Bhk.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bedroom = 2
            }
            3 -> {
                fragmentPostPropertyRentalDetails.tv3Bhk.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bedroom = 3
            }
            4 -> {
                fragmentPostPropertyRentalDetails.tv4Bhk.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bedroom = 4
            }
            else -> {
                fragmentPostPropertyRentalDetails.tv5Bhk.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bedroom = 5
            }
        }
    }

    private fun selectBathRoom(bathRoom: Int) {
        fragmentPostPropertyRentalDetails.tv1Bath.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)
        fragmentPostPropertyRentalDetails.tv2Bath.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)
        fragmentPostPropertyRentalDetails.tv3Bath.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)
        fragmentPostPropertyRentalDetails.tv4Bath.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)
        fragmentPostPropertyRentalDetails.tv5Bath.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)

        when (bathRoom) {
            1 -> {
                fragmentPostPropertyRentalDetails.tv1Bath.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bathroom = 1
            }
            2 -> {
                fragmentPostPropertyRentalDetails.tv2Bath.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bathroom = 2
            }
            3 -> {
                fragmentPostPropertyRentalDetails.tv3Bath.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bathroom = 3
            }
            4 -> {
                fragmentPostPropertyRentalDetails.tv4Bath.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bathroom = 4
            }
            else -> {
                fragmentPostPropertyRentalDetails.tv5Bath.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bathroom = 5
            }
        }
    }

    private fun setListeners() {
        fragmentPostPropertyRentalDetails.cvNext.setOnClickListener {
            if(validate()) {
                propertyRentalEntityVal?.address =
                    fragmentPostPropertyRentalDetails.tvAddress.text.toString()
                propertyRentalEntityVal?.town =
                    fragmentPostPropertyRentalDetails.etTownLocality.text.toString()
                propertyRentalEntityVal?.city =
                    fragmentPostPropertyRentalDetails.etCityArea.text.toString()
                propertyRentalEntityVal?.landmark =
                    fragmentPostPropertyRentalDetails.etLandmark.text.toString()
                propertyRentalEntityVal?.carpetArea =
                    fragmentPostPropertyRentalDetails.etCarpetArea.text.toString().toInt()
                propertyRentalEntityVal?.price =
                    fragmentPostPropertyRentalDetails.etPrice.text.toString().toInt()

                jsonUserAddress?.let { jua ->
                    propertyRentalEntityVal?.coordinates =
                        listOf(jua.latitude.toDouble(), jua.longitude.toDouble())
                }
                propertyRentalEntityVal?.rentalType =
                    fragmentPostPropertyRentalDetails.spinnerRentalType.selectedItem.toString()
                propertyRentalEntityVal?.availableFrom =
                    fragmentPostPropertyRentalDetails.tvAvailableFrom.text.toString()

                propertyRentalViewModel.insertPropertyRental(
                    requireContext(),
                    propertyRentalEntityVal
                )

                postPropertyRentalDetailsFragmentInteractionListener.goToPostPropertyRentalImageUploadFragment()
            }
        }

        fragmentPostPropertyRentalDetails.tv1Bhk.setOnClickListener {
            selectBedroom(1)
        }

        fragmentPostPropertyRentalDetails.tv2Bhk.setOnClickListener {
            selectBedroom(2)
        }

        fragmentPostPropertyRentalDetails.tv3Bhk.setOnClickListener {
            selectBedroom(3)
        }

        fragmentPostPropertyRentalDetails.tv4Bhk.setOnClickListener {
            selectBedroom(4)
        }

        fragmentPostPropertyRentalDetails.tv5Bhk.setOnClickListener {
            selectBedroom(5)
        }

        fragmentPostPropertyRentalDetails.tv1Bath.setOnClickListener {
            selectBathRoom(1)
        }

        fragmentPostPropertyRentalDetails.tv2Bath.setOnClickListener {
            selectBathRoom(2)
        }

        fragmentPostPropertyRentalDetails.tv3Bath.setOnClickListener {
            selectBathRoom(3)
        }

        fragmentPostPropertyRentalDetails.tv4Bath.setOnClickListener {
            selectBathRoom(4)
        }

        fragmentPostPropertyRentalDetails.tv5Bath.setOnClickListener {
            selectBathRoom(5)
        }

        fragmentPostPropertyRentalDetails.tvAddress.setOnClickListener {
            val setAddressIntent = Intent(requireContext(), AddAddressActivity::class.java).apply {
                putExtra(Constants.REQUEST_ADDRESS_FROM, Constants.POST_PROPERTY_RENTAL)
            }
            startForResult.launch(setAddressIntent)
        }

        fragmentPostPropertyRentalDetails.viewCityArea.setOnClickListener {
            val setAddressIntent = Intent(requireContext(), AddAddressActivity::class.java).apply {
                putExtra(Constants.REQUEST_ADDRESS_FROM, Constants.POST_PROPERTY_RENTAL)
            }
            startForResult.launch(setAddressIntent)
        }

        fragmentPostPropertyRentalDetails.tvAvailableFrom.setOnClickListener {
            val mCalendar: Calendar = Calendar.getInstance()
            val year: Int = mCalendar.get(Calendar.YEAR)
            val month: Int = mCalendar.get(Calendar.MONTH)
            val dayOfMonth: Int = mCalendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                this,
                year,
                month,
                dayOfMonth
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

    }

    private fun validate(): Boolean {
        if (fragmentPostPropertyRentalDetails.etPrice.text.isNullOrEmpty()) {
            showSnackBar("Please enter rent per month.")
            return false
        }else if (fragmentPostPropertyRentalDetails.etPrice.text.toString().toInt() <=0) {
            showSnackBar("Rent per month cannot be 0 or less than 0.")
            return false
        }else if (fragmentPostPropertyRentalDetails.etCarpetArea.text.isNullOrEmpty()) {
            showSnackBar("Please enter carpet area.")
            return false
        }else if (fragmentPostPropertyRentalDetails.etCarpetArea.text.toString().toInt() <=0) {
            showSnackBar("Carpet area cannot be 0 or less than 0.")
            return false
        }else if (fragmentPostPropertyRentalDetails.tvAvailableFrom.text.isNullOrEmpty()) {
            showSnackBar("Available from date cannot be empty.")
            return false
        }
        return true
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(
            fragmentPostPropertyRentalDetails.root, text,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val mCalendar = Calendar.getInstance()

        mCalendar[Calendar.YEAR] = year
        mCalendar[Calendar.MONTH] = month
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val selectedDate = CommonHelper.SDF_YYYY_MM_DD.format(mCalendar.time)
        Log.d("Date selected", selectedDate)

        fragmentPostPropertyRentalDetails.tvAvailableFrom.text = selectedDate
    }

    override fun onMapReady(gm: GoogleMap?) {
        gm?.let {
            googleMap = it
        }
    }
}

interface PostPropertyRentalDetailsFragmentInteractionListener {
    fun goToPostPropertyRentalImageUploadFragment()
}