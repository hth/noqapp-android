package com.noqapp.android.client.views.version_2.adapter

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ListItemNotificationBinding
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.utils.GetTimeAgoUtils
import com.noqapp.android.common.pojos.DisplayNotification
import com.noqapp.android.common.utils.CommonHelper
import com.squareup.picasso.Picasso
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class NotificationAdapter(private val notificationList: List<DisplayNotification>, val onClickListener: (DisplayNotification) -> Unit) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(private val listItemNotificationBinding: ListItemNotificationBinding) :
        RecyclerView.ViewHolder(listItemNotificationBinding.root), View.OnClickListener {

        var displayNotification: DisplayNotification? = null

        init {
            listItemNotificationBinding.ivDelete.setOnClickListener(this)
        }

        fun bind(displayNotification: DisplayNotification) {
            this.displayNotification = displayNotification
            listItemNotificationBinding.tvTitle.text = displayNotification.title

            setLinks(listItemNotificationBinding.tvMsg, displayNotification.body)

            try {
                val dateString = displayNotification.createdDate
                val startDate = Date().time - CommonHelper.stringToDate(dateString).time
                listItemNotificationBinding.tvCreate.text = GetTimeAgoUtils.getTimeInAgo(startDate)
            } catch (e: Exception) {
                e.printStackTrace()
                listItemNotificationBinding.tvCreate.text = ""
            }

            if (displayNotification.status == Constants.KEY_UNREAD) {
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

    fun setLinks(tv: TextView, text: String) {
        val linkPatterns = arrayOf(
            "([Hh][tT][tT][pP][sS]?:\\/\\/[^ ,'\">\\]\\)]*[^\\. ,'\">\\]\\)])",
            "#[\\w]+",
            "@[\\w]+"
        )

        var foundLink = false
        val f = SpannableString(text)
        for (str in linkPatterns) {
            val pattern: Pattern = Pattern.compile(str)
            val matcher: Matcher = pattern.matcher(text)
            while (matcher.find()) {
                foundLink = true

                val x: Int = matcher.start()
                val y: Int = matcher.end()

                val spanText = text.substring(x, y)
                val span = URLSpan(spanText)
                f.setSpan(span, x, y, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                tv.text = f
            }
        }

        if (!foundLink) {
            tv.text = text
        }

        tv.setLinkTextColor(Color.BLUE)
        tv.linksClickable = true
        tv.movementMethod = LinkMovementMethod.getInstance()
        tv.isFocusable = false
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
