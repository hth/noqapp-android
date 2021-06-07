package com.noqapp.android.client.views.version_2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.noqapp.android.client.databinding.FragmentNotificationBinding
import com.noqapp.android.client.model.database.utils.NotificationDB
import com.noqapp.android.client.utils.AnalyticsEvents
import com.noqapp.android.client.utils.AppUtils
import com.noqapp.android.client.utils.ShowCustomDialog
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.adapter.NotificationAdapter
import com.noqapp.android.client.views.version_2.viewmodels.HomeViewModel
import com.noqapp.android.common.pojos.DisplayNotification

class NotificationFragment: BaseFragment() {

    private lateinit var fragmentNotificationBinding: FragmentNotificationBinding

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory(requireActivity().application))[HomeViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentNotificationBinding = FragmentNotificationBinding.inflate(LayoutInflater.from(requireContext()), container, false)
        return fragmentNotificationBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadListData()
        if (AppUtils.isRelease()) {
            AnalyticsEvents.logContentEvent(AnalyticsEvents.EVENT_NOTIFICATION_SCREEN)
        }
    }

    private fun loadListData() {

        homeViewModel.notificationListLiveData.observe(viewLifecycleOwner, { notificationsList->

            val adapter = NotificationAdapter(notificationsList) {
                deleteNotification(it)
            }

            fragmentNotificationBinding.rvNotification.layoutManager = LinearLayoutManager(requireContext())
            fragmentNotificationBinding.rvNotification.adapter = adapter
            if (notificationsList.isEmpty()) {
                fragmentNotificationBinding.rvNotification.visibility = View.GONE
                fragmentNotificationBinding.rlEmpty.visibility = View.VISIBLE
            } else {
                fragmentNotificationBinding.rvNotification.visibility = View.VISIBLE
                fragmentNotificationBinding.rlEmpty.visibility = View.GONE
            }
        })
    }

    private fun deleteNotification(displayNotification: DisplayNotification) {
        val showDialog = ShowCustomDialog(requireContext(), true)
        showDialog.setDialogClickListener(object : ShowCustomDialog.DialogClickListener {
            override fun btnPositiveClick() {
                NotificationDB.deleteNotification(displayNotification.sequence, displayNotification.key)
                loadListData()
            }

            override fun btnNegativeClick() {
                //Do nothing
            }
        })
        showDialog.displayDialog("Delete Notification", "Do you want to delete this notification?")
    }

}