package com.noqapp.android.client.views.version_2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.noqapp.android.client.databinding.FragmentUnderDevelopmentBinding

class UnderDevelopmentFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentUnderDevelopmentBinding = FragmentUnderDevelopmentBinding.inflate(inflater, container, false)
        return fragmentUnderDevelopmentBinding.root
    }

}