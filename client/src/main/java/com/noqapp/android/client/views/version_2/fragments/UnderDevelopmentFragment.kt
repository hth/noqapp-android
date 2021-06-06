package com.noqapp.android.client.views.version_2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.noqapp.android.client.databinding.FragmentUnderDevelopmentBinding

class UnderDevelopmentFragment : Fragment() {

    private lateinit var fragmentUnderDevelopmentBinding: FragmentUnderDevelopmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentUnderDevelopmentBinding = FragmentUnderDevelopmentBinding.inflate(inflater, container, false)
        return fragmentUnderDevelopmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val underDevelopmentFragmentArgs = UnderDevelopmentFragmentArgs.fromBundle(it)
            fragmentUnderDevelopmentBinding.tvFutureUpdateText.text = underDevelopmentFragmentArgs.futureUpdates
        }
    }

}