package com.noqapp.android.client.views.version_2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.noqapp.android.client.databinding.FragmentNotificationBinding
import com.noqapp.android.client.utils.AnalyticsEvents
import com.noqapp.android.client.utils.AppUtils
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.utils.ShowCustomDialog
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.adapter.NotificationAdapter
import com.noqapp.android.client.views.version_2.viewmodels.HomeViewModel
import com.noqapp.android.common.pojos.DisplayNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationFragment: BaseFragment() {

    private lateinit var fragmentNotificationBinding: FragmentNotificationBinding
    private lateinit var adapter: NotificationAdapter

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory(requireActivity().application))[HomeViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentNotificationBinding = FragmentNotificationBinding.inflate(LayoutInflater.from(requireContext()), container, false)
        return fragmentNotificationBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        loadListData()
        if (AppUtils.isRelease()) {
            AnalyticsEvents.logContentEvent(AnalyticsEvents.EVENT_NOTIFICATION_SCREEN)
        }
    }

    private fun setUpRecyclerView() {
        adapter = NotificationAdapter(mutableListOf()) {
            deleteNotification(it)
        }

        fragmentNotificationBinding.rvNotification.layoutManager = LinearLayoutManager(requireContext())
        fragmentNotificationBinding.rvNotification.adapter = adapter
    }

    private fun loadListData() {

        homeViewModel.notificationListLiveData.observe(viewLifecycleOwner, { notificationsList->

            adapter.addNotifications(notificationsList)

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
                homeViewModel.deleteNotification(displayNotification.key)
                loadListData()
            }

            override fun btnNegativeClick() {
                //Do nothing
            }
        })
        showDialog.displayDialog("Delete Notification", "Do you want to delete this notification?")
    }

}
