package com.noqapp.android.client.views.version_2.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.noqapp.android.client.databinding.FragmentViewBusinessListBinding
import com.noqapp.android.client.databinding.LayoutProgressBarBinding
import com.noqapp.android.client.presenter.beans.BizStoreElastic
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery
import com.noqapp.android.client.utils.ShowAlertInformation
import com.noqapp.android.client.views.activities.StoreWithMenuActivity
import com.noqapp.android.client.views.adapters.StoreInfoViewAllAdapter
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.NavigationBundleUtils
import com.noqapp.android.client.views.version_2.viewmodels.HomeViewModel
import com.noqapp.android.client.views.version_2.viewmodels.ViewBusinessListViewModel

class ViewBusinessListFragment : BaseFragment(), StoreInfoViewAllAdapter.OnItemClickListener {

    private val TAG = ViewBusinessListFragment::class.java.simpleName
    private lateinit var fragmentViewBusinessListBinding: FragmentViewBusinessListBinding
    private lateinit var progressLoaderBinding: LayoutProgressBarBinding
    private lateinit var viewBusinessArgs: ViewBusinessListFragmentArgs
    private lateinit var storeInfoViewAllAdapter: StoreInfoViewAllAdapter

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory(requireActivity().application))[HomeViewModel::class.java]
    }

    private val viewBusinessListViewModel: ViewBusinessListViewModel by lazy {
        ViewModelProvider(this)[ViewBusinessListViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentViewBusinessListBinding = FragmentViewBusinessListBinding.inflate(LayoutInflater.from(requireContext()), container, false)
        progressLoaderBinding = LayoutProgressBarBinding.inflate(inflater)
        fragmentViewBusinessListBinding.root.addView(progressLoaderBinding.clProgressBar)

        arguments?.let {
            viewBusinessArgs = ViewBusinessListFragmentArgs.fromBundle(it)
        }
        return fragmentViewBusinessListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeData()
    }

    private fun observeData() {

        viewBusinessListViewModel.errorLiveData.observe(viewLifecycleOwner, {
            progressLoaderBinding.clProgressBar.visibility = View.GONE
            ShowAlertInformation.showErrorDialog(context, it)
        })

        homeViewModel.searchStoreQueryLiveData.observe(viewLifecycleOwner, {
            it.searchedOnBusinessType = viewBusinessArgs.businessType
            setUpRecyclerView(it)
            progressLoaderBinding.clProgressBar.visibility = View.VISIBLE
            progressLoaderBinding.tvProgressMessage.text = "Loading business list, please wait..."
            viewBusinessListViewModel.fetchBusinessList(it)
        })

        viewBusinessListViewModel.businessListResponse.observe(viewLifecycleOwner, {
            progressLoaderBinding.clProgressBar.visibility = View.GONE
            it?.let { bizStoreElasticList ->
                storeInfoViewAllAdapter.addItems(bizStoreElasticList.bizStoreElastics)
            }
        })
    }

    private fun setUpRecyclerView(searchStoreQuery: SearchStoreQuery) {
        fragmentViewBusinessListBinding.rvBusiness.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        fragmentViewBusinessListBinding.rvBusiness.layoutManager = layoutManager
        fragmentViewBusinessListBinding.rvBusiness.itemAnimator = DefaultItemAnimator()
        storeInfoViewAllAdapter = StoreInfoViewAllAdapter(mutableListOf(), requireContext(), this, searchStoreQuery.latitude.toDouble(), searchStoreQuery.longitude.toDouble())
        fragmentViewBusinessListBinding.rvBusiness.adapter = storeInfoViewAllAdapter
    }

    override fun onStoreItemClick(item: BizStoreElastic?) {
        item?.let {
            NavigationBundleUtils.navigateToStore(requireActivity(), item)
        } ?: run {
            Log.d(ViewBusinessListFragment::class.java.simpleName, "BizStoreElastic is null")
        }
    }

}
