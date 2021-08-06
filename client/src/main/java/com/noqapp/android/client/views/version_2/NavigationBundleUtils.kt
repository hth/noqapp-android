package com.noqapp.android.client.views.version_2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.noqapp.android.client.presenter.beans.BizStoreElastic
import com.noqapp.android.client.presenter.beans.body.SearchQuery
import com.noqapp.android.client.utils.IBConstant
import com.noqapp.android.client.views.activities.*
import com.noqapp.android.common.model.types.BusinessSupportEnum
import com.noqapp.android.common.model.types.BusinessTypeEnum

object NavigationBundleUtils {

    fun navigateToSearch(context: Context, searchQuery: SearchQuery) {
        val searchIntent = Intent(context, SearchActivity::class.java)
        searchIntent.putExtra("scrollId", "")
        searchIntent.putExtra("lat", searchQuery.latitude)
        searchIntent.putExtra("lng", searchQuery.longitude)
        searchIntent.putExtra("city", searchQuery.cityName)
        context.startActivity(searchIntent)
    }

    fun navigateToStore(activity: Activity, bizStoreElastic: BizStoreElastic) {
        val intent: Intent
        val bundle = Bundle()
        when (bizStoreElastic.businessType) {
            BusinessTypeEnum.DO, BusinessTypeEnum.CD, BusinessTypeEnum.CDQ, BusinessTypeEnum.BK, BusinessTypeEnum.HS, BusinessTypeEnum.PW -> {
                // open hospital/Bank profile
                bundle.putString(IBConstant.KEY_CODE_QR, bizStoreElastic.codeQR)
                bundle.putBoolean(IBConstant.KEY_FROM_LIST, false)
                bundle.putBoolean(IBConstant.KEY_CALL_CATEGORY, true)
                bundle.putBoolean(IBConstant.KEY_IS_CATEGORY, false)
                bundle.putSerializable("BizStoreElastic", bizStoreElastic)
                bundle.putBoolean(
                    IBConstant.KEY_IS_TEMPLE,
                    bizStoreElastic.businessType == BusinessTypeEnum.PW
                )
                intent = Intent(activity, CategoryInfoActivity::class.java)
                intent.putExtra("bundle", bundle)
                activity.startActivity(intent)
            }
            BusinessTypeEnum.PH -> {
                // open order screen
                intent = Intent(activity, StoreDetailActivity::class.java)
                bundle.putSerializable("BizStoreElastic", bizStoreElastic)
                intent.putExtras(bundle)
                activity.startActivity(intent)
            }
            BusinessTypeEnum.RSQ, BusinessTypeEnum.GSQ, BusinessTypeEnum.BAQ, BusinessTypeEnum.CFQ, BusinessTypeEnum.FTQ, BusinessTypeEnum.STQ ->                 //@TODO Modification done due to corona crisis, Re-check all the functionality
                //proper testing required
                if (BusinessSupportEnum.OQ == bizStoreElastic.businessType.businessSupport) {
                    intent = Intent(activity, BeforeJoinOrderQueueActivity::class.java)
                    bundle.putString(IBConstant.KEY_CODE_QR, bizStoreElastic.codeQR)
                    bundle.putBoolean(IBConstant.KEY_FROM_LIST, false)
                    bundle.putBoolean(IBConstant.KEY_IS_CATEGORY, false)
                    bundle.putSerializable("BizStoreElastic", bizStoreElastic)
                    intent.putExtras(bundle)
                    activity.startActivity(intent)
                } else {
                    Log.d(NavigationBundleUtils::class.java.simpleName, "Reached un-supported condition")
                    FirebaseCrashlytics.getInstance().log("Reached un-supported condition " + bizStoreElastic.businessType)
                }
            else -> {
                // open order screen
                intent = Intent(activity, StoreWithMenuActivity::class.java)
                bundle.putSerializable("BizStoreElastic", bizStoreElastic)
                intent.putExtras(bundle)
                activity.startActivity(intent)
            }
        }
    }
}
