package com.noqapp.android.client.views.version_2.adapter

import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ListItemNotificationBinding
import com.noqapp.android.client.model.database.utils.NotificationDB
import com.noqapp.android.client.utils.GetTimeAgoUtils
import com.noqapp.android.common.pojos.DisplayNotification
import com.noqapp.android.common.utils.CommonHelper
import com.squareup.picasso.Picasso
import java.util.*

class NotificationAdapter(private val notificationList: List<DisplayNotification>, val onClickListener: (DisplayNotification) -> Unit) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(private val listItemNotificationBinding: ListItemNotificationBinding) : RecyclerView.ViewHolder(listItemNotificationBinding.root), View.OnClickListener {

        var displayNotification: DisplayNotification? = null

        init {
            listItemNotificationBinding.ivDelete.setOnClickListener(this)
        }

        fun bind(displayNotification: DisplayNotification){
            this.displayNotification = displayNotification
            listItemNotificationBinding.tvTitle.text = displayNotification.title
            listItemNotificationBinding.tvMsg.text = displayNotification.body
            try {
                val dateString = displayNotification.notificationCreate
                val startDate = Date().time - CommonHelper.stringToDate(dateString).time
                listItemNotificationBinding.tvCreate.text = GetTimeAgoUtils.getTimeInAgo(startDate)
            } catch (e: Exception) {
                e.printStackTrace()
                listItemNotificationBinding.tvCreate.text = ""
            }

            if (displayNotification.status == NotificationDB.KEY_UNREAD) {
                listItemNotificationBinding.cardview.setCardBackgroundColor(Color.WHITE)
            } else {
                listItemNotificationBinding.cardview.setCardBackgroundColor(Color.parseColor("#f6f6f6"))
            }

            if (TextUtils.isEmpty(displayNotification.imageUrl)) {
                listItemNotificationBinding.ivBigImage.visibility = View.GONE
            } else {
                listItemNotificationBinding.ivBigImage.visibility = View.VISIBLE
                Picasso.get().load(displayNotification.imageUrl).into(listItemNotificationBinding.ivBigImage)
            }
        }

        override fun onClick(p0: View?) {
            displayNotification?.let {
                onClickListener(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_notification, parent, false)
        return NotificationViewHolder(ListItemNotificationBinding.bind(view))
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notificationList[position])
    }
}