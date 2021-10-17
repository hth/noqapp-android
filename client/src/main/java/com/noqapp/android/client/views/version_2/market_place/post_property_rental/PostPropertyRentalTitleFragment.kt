package com.noqapp.android.client.views.version_2.market_place.post_property_rental

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.FragmentPostPropertyRentalTitleBinding
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.market_place.PostPropertyRentalViewModel
import com.noqapp.android.common.pojos.PropertyRentalEntity

class PostPropertyRentalTitleFragment : BaseFragment() {

    private lateinit var fragmentPostPropertyRentalTitle: FragmentPostPropertyRentalTitleBinding
    private lateinit var postPropertyRentalViewModel: PostPropertyRentalViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPostPropertyRentalTitle = FragmentPostPropertyRentalTitleBinding.inflate(inflater, container, false)
        postPropertyRentalViewModel = ViewModelProvider(requireActivity())[PostPropertyRentalViewModel::class.java]
        return fragmentPostPropertyRentalTitle.root
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
                fragmentPostPropertyRentalTitle.etTitle.setText(propertyRentalEntity.title)
                fragmentPostPropertyRentalTitle.etDescription.setText(propertyRentalEntity.description)
            })
    }

    private fun setListeners() {
        fragmentPostPropertyRentalTitle.btnNext.setOnClickListener {
            insertPropertyRentalInDb()
            findNavController().navigate(R.id.fragment_post_property_rental_details)
        }
    }

    private fun insertPropertyRentalInDb() {
        val propertyRentalEntity = PropertyRentalEntity(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            fragmentPostPropertyRentalTitle.etTitle.text.toString(),
            fragmentPostPropertyRentalTitle.etDescription.text.toString(),
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
        postPropertyRentalViewModel.insertPropertyRental(requireContext(), propertyRentalEntity)
    }
}
