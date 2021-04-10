package com.noqapp.android.merchant.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.noqapp.android.common.beans.JsonCoupon
import com.noqapp.android.common.beans.JsonCouponList
import com.noqapp.android.common.presenter.CouponPresenter
import com.noqapp.android.merchant.R
import com.noqapp.android.merchant.model.CouponApiCalls
import com.noqapp.android.merchant.utils.IBConstant
import com.noqapp.android.merchant.utils.UserUtils
import com.noqapp.android.merchant.views.adapters.CouponAdapter

class CouponActivity : BaseActivity(), CouponAdapter.OnItemClickListener, CouponPresenter {

    private var couponApiCalls: CouponApiCalls? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setScreenOrientation()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discount)
        couponApiCalls = CouponApiCalls()
        couponApiCalls!!.setCouponPresenter(this)


        initActionsViews(false)
        tv_toolbar_title.text = getString(R.string.activity_discount)

        showProgress()
        setProgressMessage("Getting coupons...")
        couponApiCalls!!.availableDiscount(
                UserUtils.getDeviceId(),
                UserUtils.getEmail(),
                UserUtils.getAuth(),
                intent.getStringExtra(IBConstant.KEY_CODE_QR))
    }

    override fun discountItemClick(jsonCoupon: JsonCoupon?) {
        val intent = Intent()
        intent.putExtra(IBConstant.KEY_OBJECT, jsonCoupon)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun couponResponse(jsonCouponList: JsonCouponList?) {
        dismissProgress()
        if (null != jsonCouponList) {
            // call api
            Log.e("couponResponse", jsonCouponList.toString())
            if (jsonCouponList.coupons.size == 0) {
                val tv_queue_name = findViewById<TextView>(R.id.tv_queue_name)
                val rl_empty = findViewById<RelativeLayout>(R.id.rl_empty)
            } else {
                val couponAdapter = CouponAdapter(this, jsonCouponList.coupons, this)
                val rcv_review = findViewById<RecyclerView>(R.id.rcv_review)
                rcv_review.setHasFixedSize(true)
                if (LaunchActivity.isTablet) {
                    rcv_review.layoutManager = GridLayoutManager(this, 2);
                } else {
                    rcv_review.layoutManager = GridLayoutManager(this, 1);
                }

                rcv_review.itemAnimator = DefaultItemAnimator()
                rcv_review.adapter = couponAdapter
            }
        }
    }
}
