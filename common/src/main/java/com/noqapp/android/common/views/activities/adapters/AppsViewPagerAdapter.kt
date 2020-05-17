package com.noqapp.android.common.views.activities.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.noqapp.android.common.R
import com.noqapp.android.common.pojos.AppLinksInfos
import com.noqapp.android.common.utils.CommonHelper
import com.squareup.picasso.Picasso
import java.util.*

class AppsViewPagerAdapter(private val activity: Activity, private val appLinksInfos: ArrayList<AppLinksInfos>) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.lay_slider_apps, container, false)
        val ivBanner = view.findViewById<ImageView>(R.id.iv_banner)
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val btnDownload = view.findViewById<Button>(R.id.btn_download)
        val tvDescription = view.findViewById<TextView>(R.id.tv_description)
        val url = appLinksInfos[position].appBannerUrl
        Picasso.get().load(url).into(ivBanner)
        tvTitle.text = appLinksInfos[position].title
        tvDescription.text = appLinksInfos[position].description
        btnDownload.setOnClickListener { v: View? -> CommonHelper.openPlayStore(activity, appLinksInfos[position].appPackageId) }
        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return appLinksInfos.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }

}