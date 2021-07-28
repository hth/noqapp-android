package com.noqapp.android.client.views.version_2.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.FragmentChangeLocationBinding
import com.noqapp.android.client.location.LocationManager
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery
import com.noqapp.android.client.utils.AnalyticsEvents
import com.noqapp.android.client.utils.AppUtils
import com.noqapp.android.client.views.activities.AppInitialize
import com.noqapp.android.client.views.adapters.GooglePlacesAutocompleteAdapter
import com.noqapp.android.client.views.version_2.viewmodels.HomeViewModel

class ChangeLocationFragmentNew : Fragment(),
        (String?, String?, String?, String?, String?, String?, String?, Double, Double) -> Unit {

    private lateinit var changeLocationBinding: FragmentChangeLocationBinding

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        changeLocationBinding = FragmentChangeLocationBinding.inflate(inflater, container, false)
        return changeLocationBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeLocationBinding.toolbar.visibility = View.GONE
        changeLocationBinding.tvAuto.visibility = View.VISIBLE

        changeLocationBinding.tvAuto.setOnClickListener {
            LocationManager.fetchCurrentLocationAddress(requireContext(), this)
        }

        setUpAutoCompleteTextView()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpAutoCompleteTextView() {
        changeLocationBinding.autoCompleteTextView.setAdapter(
            GooglePlacesAutocompleteAdapter(
                requireActivity(),
                R.layout.list_item
            )
        )
        changeLocationBinding.autoCompleteTextView.setOnItemClickListener { parent: AdapterView<*>, _: View?, position: Int, _: Long ->
            try {
                val cityName = parent.getItemAtPosition(position) as String
                val geoIP = AppUtils.getLocationFromAddress(activity, cityName)
                val searchStoreQuery = SearchStoreQuery()
                searchStoreQuery.latitude = geoIP.latitude.toString()
                searchStoreQuery.longitude = geoIP.longitude.toString()
                searchStoreQuery.cityName = cityName
                searchStoreQuery.filters = ""
                searchStoreQuery.scrollId = ""
                homeViewModel.searchStoreQueryLiveData.value = searchStoreQuery
                AppInitialize.setLocationChangedManually(true)
                AppUtils.hideKeyBoard(activity)
                findNavController().navigateUp()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        changeLocationBinding.autoCompleteTextView.threshold = 3
        changeLocationBinding.autoCompleteTextView.setOnTouchListener { v: View?, event: MotionEvent ->
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_LEFT = 0
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= changeLocationBinding.autoCompleteTextView.getRight() - changeLocationBinding.autoCompleteTextView.getCompoundDrawables()
                        .get(DRAWABLE_RIGHT).getBounds().width()
                ) {
                    changeLocationBinding.autoCompleteTextView.setText("")
                }
            }
            false
        }
        if (AppUtils.isRelease()) {
            AnalyticsEvents.logContentEvent(AnalyticsEvents.EVENT_CHANGE_LOCATION)
        }
        changeLocationBinding.autoCompleteTextView.requestFocus()
        val im =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    override fun invoke(
        address: String?,
        countryShortName: String?,
        area: String?,
        town: String?,
        district: String?,
        state: String?,
        stateShortName: String?,
        latitude: Double,
        longitude: Double
    ) {
        AppUtils.setAutoCompleteText(changeLocationBinding.autoCompleteTextView, town)

        val searchStoreQuery = SearchStoreQuery()
        searchStoreQuery.cityName = AppUtils.getLocationAsString(area, town)
        searchStoreQuery.latitude = latitude.toString()
        searchStoreQuery.longitude = longitude.toString()
        searchStoreQuery.filters = ""
        searchStoreQuery.scrollId = ""
        homeViewModel.searchStoreQueryLiveData.value = searchStoreQuery
        AppInitialize.setLocationChangedManually(false)

        AppUtils.hideKeyBoard(requireActivity())
        findNavController().navigateUp()
        return
    }

}
