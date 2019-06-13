package com.noqapp.android.merchant.views.activities


import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.noqapp.android.common.beans.ErrorEncounteredJson
import com.noqapp.android.common.customviews.CustomToast
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum
import com.noqapp.android.merchant.R
import com.noqapp.android.merchant.model.CouponApiCalls
import com.noqapp.android.common.beans.JsonCoupon
import com.noqapp.android.common.beans.JsonCouponList
import com.noqapp.android.merchant.utils.AppUtils
import com.noqapp.android.merchant.utils.ErrorResponseHandler
import com.noqapp.android.merchant.utils.IBConstant
import com.noqapp.android.merchant.utils.UserUtils
import com.noqapp.android.merchant.views.adapters.CouponAdapter
import com.noqapp.android.merchant.views.interfaces.CouponPresenter


class CouponActivity : AppCompatActivity(), CouponAdapter.OnItemClickListener, CouponPresenter {

    private var progressDialog: ProgressDialog? = null
    private var couponApiCalls: CouponApiCalls? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppUtils().isTablet(applicationContext)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discount)
        couponApiCalls = CouponApiCalls()
        couponApiCalls!!.setCouponPresenter(this)

        initProgress()

        val fl_notification = findViewById<FrameLayout>(R.id.fl_notification)
        val tv_toolbar_title = findViewById<TextView>(R.id.tv_toolbar_title)
        val actionbarBack = findViewById<ImageView>(R.id.actionbarBack)
        fl_notification.visibility = View.INVISIBLE
        actionbarBack.setOnClickListener { finish() }
        tv_toolbar_title.text = getString(R.string.activity_discount)

        progressDialog!!.show()
        couponApiCalls!!.availableDiscount(UserUtils.getDeviceId(), UserUtils.getEmail(),
                UserUtils.getAuth(), intent.getStringExtra(IBConstant.KEY_CODE_QR))
    }

    private fun initProgress() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.isIndeterminate = true
        progressDialog!!.setMessage("Updating data...")
    }

    protected fun dismissProgress() {
        if (null != progressDialog && progressDialog!!.isShowing)
            progressDialog!!.dismiss()
    }

    override fun discountItemClick(jsonCoupon: JsonCoupon?) {
        val intent = Intent()
        intent.putExtra(IBConstant.KEY_OBJECT,jsonCoupon)
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
                if (AppUtils().isTablet(applicationContext)) {
                    rcv_review.layoutManager = GridLayoutManager(this, 2);
                } else {
                    rcv_review.layoutManager = GridLayoutManager(this, 1);
                }

                rcv_review.itemAnimator = DefaultItemAnimator()
                rcv_review.adapter = couponAdapter
            }
        }
    }

    override fun authenticationFailure() {
        dismissProgress()
        AppUtils.authenticationProcessing()
        finish()
    }

    override fun responseErrorPresenter(errorCode: Int) {
        dismissProgress()
        ErrorResponseHandler().processFailureResponseCode(this, errorCode)
    }

    override fun responseErrorPresenter(eej: ErrorEncounteredJson?) {
        if (null != eej) {
            if (eej.systemErrorCode == MobileSystemErrorCodeEnum.ACCOUNT_INACTIVE.code) {
                CustomToast().showToast(this, getString(R.string.error_account_block))
                LaunchActivity.getLaunchActivity().clearLoginData(false)
                dismissProgress()
                finish()//close the current activity
            } else {
                ErrorResponseHandler().processError(this, eej)
            }
        }
    }
}
