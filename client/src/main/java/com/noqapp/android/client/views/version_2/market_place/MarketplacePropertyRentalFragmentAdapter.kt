package com.noqapp.android.client.views.version_2.market_place

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MarketplacePropertyRentalFragmentAdapter(private val fragmentList: MutableList<Fragment>, fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun addFragments(fragments: List<Fragment>) {
        fragmentList.clear()
        fragmentList.addAll(fragments)
        notifyDataSetChanged()
    }

}