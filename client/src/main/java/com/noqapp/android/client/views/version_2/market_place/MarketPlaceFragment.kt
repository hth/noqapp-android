package com.noqapp.android.client.views.version_2.market_place

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.noqapp.android.client.databinding.FragmentMarketPlaceBinding
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.market_place.MarketPlaceActivity
import com.noqapp.android.client.views.version_2.market_place.post_market_place.PostMarketPlaceActivity

class MarketPlaceFragment : BaseFragment() {

    private lateinit var fragmentMarketPlaceBinding: FragmentMarketPlaceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMarketPlaceBinding = FragmentMarketPlaceBinding.inflate(inflater, container, false)
        return fragmentMarketPlaceBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        setListeners()
    }

    private fun setListeners() {
        fragmentMarketPlaceBinding.btnSearch.setOnClickListener {
            startActivity(Intent(requireContext(), MarketPlaceActivity::class.java))
        }

        fragmentMarketPlaceBinding.cvPostAProperty.setOnClickListener {
            startActivity(Intent(requireContext(), PostMarketPlaceActivity::class.java))
        }

        fragmentMarketPlaceBinding.cvMyPosts.setOnClickListener {
            startActivity(Intent(requireContext(), MarketPlaceActivity::class.java))
        }

        fragmentMarketPlaceBinding.ivClose.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}