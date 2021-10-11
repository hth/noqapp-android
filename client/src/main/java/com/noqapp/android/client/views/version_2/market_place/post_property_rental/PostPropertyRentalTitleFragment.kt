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
import javax.inject.Inject

class PostPropertyRentalTitleFragment : BaseFragment() {

    private lateinit var fragmentPostPropertyRentalTitle: FragmentPostPropertyRentalTitleBinding
    private lateinit var postPropertyRentalViewModel: PostPropertyRentalViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPostPropertyRentalTitle =
            FragmentPostPropertyRentalTitleBinding.inflate(inflater, container, false)
        postPropertyRentalViewModel =
            ViewModelProvider(requireActivity())[PostPropertyRentalViewModel::class.java]
        return fragmentPostPropertyRentalTitle.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        setListeners()
    }

    private fun setListeners() {
        fragmentPostPropertyRentalTitle.btnNext.setOnClickListener {
            findNavController().navigate(R.id.fragment_post_property_rental_details)
        }
    }
}