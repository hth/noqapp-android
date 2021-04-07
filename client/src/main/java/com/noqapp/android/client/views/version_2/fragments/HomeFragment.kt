package com.noqapp.android.client.views.version_2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.FragmentHomeNewBinding
import com.noqapp.android.client.presenter.TokenAndQueuePresenter
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueueList
import com.noqapp.android.client.views.adapters.TokenAndQueueAdapter
import com.noqapp.android.client.views.fragments.BaseFragment

class HomeFragment : BaseFragment(), TokenAndQueuePresenter {

    private lateinit var fragmentHomeNewBinding: FragmentHomeNewBinding
    private lateinit var tokenAndQueueAndQueueAdapter: TokenAndQueueAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentHomeNewBinding = FragmentHomeNewBinding.bind(LayoutInflater.from(context).inflate(R.layout.fragment_home_new, container, false))
        return fragmentHomeNewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPager()
    }

    private fun setUpViewPager() {
        tokenAndQueueAndQueueAdapter = TokenAndQueueAdapter(requireContext(), mutableListOf())
        fragmentHomeNewBinding.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        fragmentHomeNewBinding.viewpager.adapter = tokenAndQueueAndQueueAdapter
    }

    private fun addIndicator() {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.view_indicator, null)
        fragmentHomeNewBinding.llIndicator.addView(view)
    }

    override fun currentQueueResponse(tokenAndQueuesList: JsonTokenAndQueueList?) {
        tokenAndQueuesList?.let {
            tokenAndQueueAndQueueAdapter.addItems(tokenAndQueuesList.tokenAndQueues)
            fragmentHomeNewBinding.llIndicator.removeAllViews()
            tokenAndQueuesList.tokenAndQueues.forEach { _ ->
                addIndicator()
            }
        }
    }

    override fun historyQueueError() {
    }

    override fun currentQueueError() {
        fragmentHomeNewBinding.llIndicator.removeAllViews()
    }

    override fun historyQueueResponse(tokenAndQueues: MutableList<JsonTokenAndQueue>?, sinceBeginning: Boolean) {
    }

}