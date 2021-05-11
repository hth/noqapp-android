package com.noqapp.android.client.views.version_2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.noqapp.android.client.databinding.FragmentViewBusinessListBinding
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.viewmodels.HomeViewModel

class ViewBusinessListFragment : BaseFragment() {

    private val TAG = ViewBusinessListFragment::class.java.simpleName
    private lateinit var fragmentViewBusinessListBinding: FragmentViewBusinessListBinding
    private lateinit var viewBusinessArgs: ViewBusinessListFragmentArgs
    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory(requireActivity().application))[HomeViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentViewBusinessListBinding = FragmentViewBusinessListBinding.inflate(LayoutInflater.from(requireContext()), container, false)
        arguments?.let {
            viewBusinessArgs = ViewBusinessListFragmentArgs.fromBundle(it)
        }

        return fragmentViewBusinessListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        observeData()
    }

    private fun observeData() {
        homeViewModel.searchStoreQueryLiveData.observe(viewLifecycleOwner, Observer {
            it.searchedOnBusinessType = viewBusinessArgs.businessType
            homeViewModel.fetchBusinessList(it)
        })

        homeViewModel.businessListResponse.observe(viewLifecycleOwner, Observer {

        })
    }

    private fun setUpRecyclerView() {
        fragmentViewBusinessListBinding.rvBusiness.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        fragmentViewBusinessListBinding.rvBusiness.layoutManager = layoutManager
        fragmentViewBusinessListBinding.rvBusiness.itemAnimator = DefaultItemAnimator()
    }

}