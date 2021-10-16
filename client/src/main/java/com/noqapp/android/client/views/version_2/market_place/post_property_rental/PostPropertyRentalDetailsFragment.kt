package com.noqapp.android.client.views.version_2.market_place.post_property_rental

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.FragmentPostPropertyRentalDetailsBinding
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.market_place.PostPropertyRentalViewModel
import com.noqapp.android.common.model.types.category.RentalTypeEnum
import com.noqapp.android.common.pojos.PropertyRentalEntity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.widget.DatePicker
import java.util.*
import org.joda.time.DateTimeFieldType.dayOfMonth
import java.text.DateFormat


class PostPropertyRentalDetailsFragment : BaseFragment(), OnDateSetListener {

    private lateinit var fragmentPostPropertyRentalDetails: FragmentPostPropertyRentalDetailsBinding
    private lateinit var postPropertyRentalViewModel: PostPropertyRentalViewModel

    private var propertyRentalEntityVal: PropertyRentalEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPostPropertyRentalDetails =
            FragmentPostPropertyRentalDetailsBinding.inflate(inflater, container, false)
        postPropertyRentalViewModel =
            ViewModelProvider(requireActivity())[PostPropertyRentalViewModel::class.java]
        return fragmentPostPropertyRentalDetails.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }
        observeData()
        setListeners()
    }

    private fun observeData() {
        postPropertyRentalViewModel.getPropertyRental(requireContext())
            .observe(viewLifecycleOwner, {
                val propertyRentalEntity = it[0]
                propertyRentalEntityVal = propertyRentalEntity
                fragmentPostPropertyRentalDetails.etAddress.setText(propertyRentalEntity.address)
                fragmentPostPropertyRentalDetails.etLandmark.setText(propertyRentalEntity.landmark)
                fragmentPostPropertyRentalDetails.etTownLocality.setText(propertyRentalEntity.town)
                fragmentPostPropertyRentalDetails.etCityArea.setText(propertyRentalEntity.city)
                fragmentPostPropertyRentalDetails.etCarpetArea.setText(propertyRentalEntity.carpetArea!!)
                fragmentPostPropertyRentalDetails.etRentPerMonth.setText(propertyRentalEntity.price!!)
                fragmentPostPropertyRentalDetails.tvAvailableFrom.text = propertyRentalEntity.availableFrom

                propertyRentalEntity.rentalType?.let { rentalType ->
                    when (rentalType) {
                        RentalTypeEnum.A -> {
                            fragmentPostPropertyRentalDetails.spinnerRentalType.setSelection(0)
                        }
                        RentalTypeEnum.H -> {
                            fragmentPostPropertyRentalDetails.spinnerRentalType.setSelection(1)
                        }
                        RentalTypeEnum.R -> {
                            fragmentPostPropertyRentalDetails.spinnerRentalType.setSelection(2)
                        }
                        RentalTypeEnum.T -> {
                            fragmentPostPropertyRentalDetails.spinnerRentalType.setSelection(3)
                        }
                    }
                }

                propertyRentalEntity.bedroom?.let { bedRoom ->
                    selectBedroom(bedRoom)
                }

                propertyRentalEntity.bathroom?.let { bathRoom ->
                    selectBathRoom(bathRoom)
                }

            })
    }

    private fun selectBedroom(bedRoom: Int) {
        fragmentPostPropertyRentalDetails.tv1Bhk.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)
        fragmentPostPropertyRentalDetails.tv2Bhk.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)
        fragmentPostPropertyRentalDetails.tv3Bhk.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)
        fragmentPostPropertyRentalDetails.tv4Bhk.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)
        fragmentPostPropertyRentalDetails.tv5Bhk.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)

        when (bedRoom) {
            1 -> {
                fragmentPostPropertyRentalDetails.tv1Bhk.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bedroom = 1
            }
            2 -> {
                fragmentPostPropertyRentalDetails.tv2Bhk.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bedroom = 2
            }
            3 -> {
                fragmentPostPropertyRentalDetails.tv3Bhk.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bedroom = 3
            }
            4 -> {
                fragmentPostPropertyRentalDetails.tv4Bhk.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bedroom = 4
            }
            else -> {
                fragmentPostPropertyRentalDetails.tv5Bhk.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bedroom = 5
            }
        }
    }

    private fun selectBathRoom(bathRoom: Int) {
        fragmentPostPropertyRentalDetails.tv1Bath.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)
        fragmentPostPropertyRentalDetails.tv2Bath.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)
        fragmentPostPropertyRentalDetails.tv3Bath.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)
        fragmentPostPropertyRentalDetails.tv4Bath.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)
        fragmentPostPropertyRentalDetails.tv5Bath.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner_cyan_background)

        when (bathRoom) {
            1 -> {
                fragmentPostPropertyRentalDetails.tv1Bath.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bathroom = 1
            }
            2 -> {
                fragmentPostPropertyRentalDetails.tv2Bath.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bathroom = 2
            }
            3 -> {
                fragmentPostPropertyRentalDetails.tv3Bath.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bathroom = 3
            }
            4 -> {
                fragmentPostPropertyRentalDetails.tv4Bath.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bathroom = 4
            }
            else -> {
                fragmentPostPropertyRentalDetails.tv5Bath.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
                propertyRentalEntityVal?.bathroom = 5
            }
        }
    }

    private fun setListeners() {
        fragmentPostPropertyRentalDetails.btnNext.setOnClickListener {
            propertyRentalEntityVal?.address = fragmentPostPropertyRentalDetails.etAddress.text.toString()
            propertyRentalEntityVal?.town = fragmentPostPropertyRentalDetails.etAddress.text.toString()
            propertyRentalEntityVal?.city = fragmentPostPropertyRentalDetails.etAddress.text.toString()
            propertyRentalEntityVal?.landmark = fragmentPostPropertyRentalDetails.etAddress.text.toString()
            propertyRentalEntityVal?.carpetArea = fragmentPostPropertyRentalDetails.etCarpetArea.text.toString().toInt()
            propertyRentalEntityVal?.price = fragmentPostPropertyRentalDetails.etRentPerMonth.text.toString().toInt()
            propertyRentalEntityVal?.rentalType = fragmentPostPropertyRentalDetails.spinnerRentalType.selectedItem as RentalTypeEnum
            propertyRentalEntityVal?.availableFrom = fragmentPostPropertyRentalDetails.tvAvailableFrom.text.toString()

            postPropertyRentalViewModel.insertPropertyRental(requireContext(), propertyRentalEntityVal)
            findNavController().navigate(R.id.fragment_post_property_rental_image)
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
            selectBedroom(1)
        }

        fragmentPostPropertyRentalDetails.tv2Bath.setOnClickListener {
            selectBedroom(2)
        }

        fragmentPostPropertyRentalDetails.tv3Bath.setOnClickListener {
            selectBedroom(3)
        }

        fragmentPostPropertyRentalDetails.tv4Bath.setOnClickListener {
            selectBedroom(4)
        }

        fragmentPostPropertyRentalDetails.tv5Bath.setOnClickListener {
            selectBedroom(5)
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
            datePickerDialog.show()
        }

    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val mCalendar = Calendar.getInstance()

        mCalendar[Calendar.YEAR] = year
        mCalendar[Calendar.MONTH] = month
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val selectedDate: String =
            DateFormat.getDateInstance(DateFormat.FULL).format(mCalendar.time)

        fragmentPostPropertyRentalDetails.tvAvailableFrom.text = selectedDate
    }
}