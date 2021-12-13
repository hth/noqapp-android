package com.noqapp.android.client.views.version_2.market_place.householdItem.post_household_item

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.FragmentPostHouseHoldItemDetailsBinding
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.market_place.householdItem.HouseholdItemViewModel
import com.noqapp.android.common.pojos.HouseHoldItemEntity
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
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
import com.noqapp.android.common.model.types.category.HouseholdItemCategoryEnum
import com.noqapp.android.common.model.types.category.ItemConditionEnum

class PostHouseHoldItemDetailsFragment : BaseFragment(), OnDateSetListener, OnMapReadyCallback {

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val jsonUserAddress = intent?.getSerializableExtra(Constants.JSON_USER_ADDRESS) as JsonUserAddress
                this.jsonUserAddress = jsonUserAddress
                if (jsonUserAddress.address != null && jsonUserAddress.address != "") {
                    fragmentPostHouseHoldItemDetailsBinding.tvAddress.text = jsonUserAddress.address
                }
                fragmentPostHouseHoldItemDetailsBinding.etCityArea.setText(jsonUserAddress.area.toString())
                fragmentPostHouseHoldItemDetailsBinding.etTownLocality.setText(jsonUserAddress.town.toString())

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

    private lateinit var fragmentPostHouseHoldItemDetailsBinding: FragmentPostHouseHoldItemDetailsBinding
    private lateinit var householdItemViewModel: HouseholdItemViewModel
    private lateinit var postHouseHoldItemDetailsFragmentInteractionListener: PostHouseHoldItemDetailsFragmentInteractionListener
    private var jsonUserAddress: JsonUserAddress? = null
    private var houseHoldItemEntityVal: HouseHoldItemEntity? = null
    private lateinit var googleMap: GoogleMap

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PostHouseHoldItemDetailsFragmentInteractionListener) {
            postHouseHoldItemDetailsFragmentInteractionListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPostHouseHoldItemDetailsBinding = FragmentPostHouseHoldItemDetailsBinding.inflate(inflater, container, false)
        householdItemViewModel = ViewModelProvider(requireActivity())[HouseholdItemViewModel::class.java]
        return fragmentPostHouseHoldItemDetailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }
        observeData()
        setListeners()
        val spItemConditionAdapter = ArrayAdapter(requireActivity(),android.R.layout.simple_spinner_item, ItemConditionEnum.asListOfDescription())
        fragmentPostHouseHoldItemDetailsBinding.spinnerRentalType.adapter = spItemConditionAdapter
        val spItemCategoryAdapter = ArrayAdapter(requireActivity(),android.R.layout.simple_spinner_item, HouseholdItemCategoryEnum.asListOfDescription())
        fragmentPostHouseHoldItemDetailsBinding.spinnerItemCategoryType.adapter = spItemCategoryAdapter
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun observeData() {
        householdItemViewModel.getHouseHoldItem(requireContext())
            .observe(viewLifecycleOwner, {
                if (it.isNotEmpty()) {
                    val houseHoldItemEntity = it[0]
                    houseHoldItemEntityVal = houseHoldItemEntity

                    if (houseHoldItemEntity.address != null && houseHoldItemEntity.address != "") {
                        fragmentPostHouseHoldItemDetailsBinding.tvAddress.text = houseHoldItemEntity.address
                    }

                    if (houseHoldItemEntity.coordinates[0] != 0.0) {
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    houseHoldItemEntity.coordinates[0],
                                    houseHoldItemEntity.coordinates[1]
                                ), 16.0f
                            )
                        )
                    }

                    fragmentPostHouseHoldItemDetailsBinding.etLandmark.setText(houseHoldItemEntity.landmark)
                    fragmentPostHouseHoldItemDetailsBinding.etTownLocality.setText(houseHoldItemEntity.town)
                    fragmentPostHouseHoldItemDetailsBinding.etCityArea.setText(houseHoldItemEntity.city)
                    fragmentPostHouseHoldItemDetailsBinding.etPrice.setText(houseHoldItemEntity.price.toString())
                    fragmentPostHouseHoldItemDetailsBinding.spinnerRentalType.setSelection(ItemConditionEnum.asListOfDescription().indexOf(houseHoldItemEntity.itemConditionType))
                    fragmentPostHouseHoldItemDetailsBinding.spinnerItemCategoryType.setSelection(HouseholdItemCategoryEnum.asListOfDescription().indexOf(houseHoldItemEntity.householdItemCategory))
                }
            })
    }


    private fun setListeners() {
        fragmentPostHouseHoldItemDetailsBinding.cvNext.setOnClickListener {
            if(validate()) {
                houseHoldItemEntityVal?.address =
                    fragmentPostHouseHoldItemDetailsBinding.tvAddress.text.toString()
                houseHoldItemEntityVal?.town =
                    fragmentPostHouseHoldItemDetailsBinding.etTownLocality.text.toString()
                houseHoldItemEntityVal?.city =
                    fragmentPostHouseHoldItemDetailsBinding.etCityArea.text.toString()
                houseHoldItemEntityVal?.landmark =
                    fragmentPostHouseHoldItemDetailsBinding.etLandmark.text.toString()
                houseHoldItemEntityVal?.price =
                    fragmentPostHouseHoldItemDetailsBinding.etPrice.text.toString().toInt()

                jsonUserAddress?.let { jua ->
                    houseHoldItemEntityVal?.coordinates =
                        listOf(jua.latitude.toDouble(), jua.longitude.toDouble())
                }

                houseHoldItemEntityVal?.itemConditionType =
                    fragmentPostHouseHoldItemDetailsBinding.spinnerRentalType.selectedItem.toString()
                houseHoldItemEntityVal?.householdItemCategory =
                    fragmentPostHouseHoldItemDetailsBinding.spinnerItemCategoryType.selectedItem.toString()

                householdItemViewModel.insertHouseHoldItem(requireContext(), houseHoldItemEntityVal)

                postHouseHoldItemDetailsFragmentInteractionListener.goToHouseHoldItemImageUploadFragment()
            }
        }

        fragmentPostHouseHoldItemDetailsBinding.tvAddress.setOnClickListener {
            val setAddressIntent = Intent(requireContext(), AddAddressActivity::class.java).apply {
                putExtra(Constants.REQUEST_ADDRESS_FROM, Constants.POST_PROPERTY_RENTAL)
            }
            startForResult.launch(setAddressIntent)
        }

        fragmentPostHouseHoldItemDetailsBinding.viewCityArea.setOnClickListener {
            val setAddressIntent = Intent(requireContext(), AddAddressActivity::class.java).apply {
                putExtra(Constants.REQUEST_ADDRESS_FROM, Constants.POST_PROPERTY_RENTAL)
            }
            startForResult.launch(setAddressIntent)
        }
    }

    private fun validate(): Boolean {
        if (fragmentPostHouseHoldItemDetailsBinding.etPrice.text.isNullOrEmpty()) {
            showSnackBar("Please enter item price.")
            return false
        }else if (fragmentPostHouseHoldItemDetailsBinding.etPrice.text.toString().toInt() <=0) {
            showSnackBar("Item price cannot be 0 or less than 0.")
            return false
        }
        return true
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(
            fragmentPostHouseHoldItemDetailsBinding.root, text,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

    }

    override fun onMapReady(gm: GoogleMap?) {
        gm?.let {
            googleMap = it
        }
    }
}

interface PostHouseHoldItemDetailsFragmentInteractionListener {
    fun goToHouseHoldItemImageUploadFragment()
}