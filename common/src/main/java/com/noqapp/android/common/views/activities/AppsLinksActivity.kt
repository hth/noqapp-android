package com.noqapp.android.common.views.activities


import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.noqapp.android.common.R
import com.noqapp.android.common.pojos.AppLinksInfos
import com.noqapp.android.common.views.activities.adapters.AppsViewPagerAdapter


class AppsLinksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_aaps_links)
        val actionbarBack: ImageView = findViewById(R.id.actionbarBack)
        actionbarBack.setOnClickListener { finish() }
        val imageList: ArrayList<AppLinksInfos> = initData()


        val pager = findViewById<ViewPager>(R.id.photos_viewpager)
        val adapter: PagerAdapter = AppsViewPagerAdapter(this@AppsLinksActivity, imageList)
        pager.adapter = adapter
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setupWithViewPager(pager, true)
    }


    private fun initData(): ArrayList<AppLinksInfos>{
        val appLinksInfos: ArrayList<AppLinksInfos> = ArrayList()
        // Client
        appLinksInfos.add(AppLinksInfos()
                .setTitle("NoQueue")
                .setDescription("enables Patients/Customers to search, pay and join service queues " +
                        "on mobile from the comfort of their home. Receives electronic tokens, " +
                        "updates on the status in queue along with expected wait times, " +
                        "currently serving token number and other details on mobile.")
                .setAppBannerUrl("https://cmg-change.com/wp-content/uploads/2018/05/Queuing-in-shop.jpg")
                .setAppPackageId("com.noqapp.android.client"));

        // Merchant hospital
        appLinksInfos.add(AppLinksInfos()
                .setTitle("NoQueue Merchant HealthCare")
                .setDescription("enables Patients/Customers to search, pay and join service queues " +
                        "on mobile from the comfort of their home. Receives electronic tokens, " +
                        "updates on the status in queue along with expected wait times, " +
                        "currently serving token number and other details on mobile.")
                .setAppBannerUrl("https://thumbs.dreamstime.com/z/cartoon-queue-hospital-inside-interior-people-vector-concept-element-flat-design-style-illustration-professional-clinic-144099341.jpg")
                .setAppPackageId("com.noqapp.android.merchant.healthcare"));

        // Merchant Store
        appLinksInfos.add(AppLinksInfos()
                .setTitle("NoQueue Merchant - Your Mobile Store")
                .setDescription("NoQueue Platform Virtual Queues\n" +
                        "are open for Govt. Services & Businesses like Banks, Kirana Stores, Distribution Centers, Lab Test Centers, Food & Entertainment Industry.\n" +
                        "\n" +
                        "Customers & Businesses may use their existing smart phones to download NoQueue mobile apps to join the system. No new hardware is required.")
                .setAppBannerUrl("https://www.retailflux.com/wp-content/uploads/2019/12/queue_management_system-min.png")
                .setAppPackageId("com.noqapp.android.merchant"));

        // Merchant TV
        appLinksInfos.add(AppLinksInfos()
                .setTitle("NoQueue Merchant TV")
                .setDescription("NoQueue Platform Virtual Queues\n" +
                        "Show the current queue status on TV\n" +
                        "Show the current customer name & phone number on tv\n" +
                        "Display custom Ad's & Announcement on TV ")
                .setAppBannerUrl("https://5.imimg.com/data5/GK/RC/AB/SELLER-24874405/queue-management-display-system-500x500.jpeg")
                .setAppPackageId("com.noqapp.android.merchant.tv"));


        return appLinksInfos;
    }

}
