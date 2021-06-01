package com.noqapp.android.client.views.version_2.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.noqapp.android.client.BuildConfig
import com.noqapp.android.client.databinding.FragmentFavouritesBinding
import com.noqapp.android.client.databinding.LayoutProgressBarBinding
import com.noqapp.android.client.presenter.beans.BizStoreElastic
import com.noqapp.android.client.utils.AppUtils
import com.noqapp.android.client.utils.IBConstant
import com.noqapp.android.client.views.activities.BeforeJoinActivity
import com.noqapp.android.client.views.activities.BeforeJoinOrderQueueActivity
import com.noqapp.android.client.views.activities.StoreDetailActivity
import com.noqapp.android.client.views.activities.StoreWithMenuActivity
import com.noqapp.android.client.views.adapters.StoreInfoViewAllAdapter
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.viewmodels.HomeViewModel
import com.noqapp.android.common.beans.ErrorEncounteredJson
import com.noqapp.android.common.model.types.BusinessSupportEnum
import com.noqapp.android.common.model.types.BusinessTypeEnum

class FavouritesFragment : BaseFragment(), StoreInfoViewAllAdapter.OnItemClickListener {

    private val TAG = FavouritesFragment::class.java.simpleName
    private lateinit var fragmentFavouritesBinding: FragmentFavouritesBinding
    private lateinit var progressLoaderBinding: LayoutProgressBarBinding

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory(requireActivity().application))[HomeViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentFavouritesBinding = FragmentFavouritesBinding.inflate(LayoutInflater.from(requireContext()), container, false)
        progressLoaderBinding = LayoutProgressBarBinding.inflate(inflater)
        fragmentFavouritesBinding.root.addView(progressLoaderBinding.clProgressBar)
        return fragmentFavouritesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        progressLoaderBinding.clProgressBar.visibility = View.VISIBLE
        fragmentFavouritesBinding.rlEmpty.visibility = View.GONE
        progressLoaderBinding.tvProgressMessage.text = "Fetching the favourite list..."
        homeViewModel.fetchFavouritesRecentVisitList(requireContext())

        observeValues()
    }

    private fun observeValues() {

        homeViewModel.favoritesListResponseLiveData.observe(viewLifecycleOwner, { it ->

            progressLoaderBinding.clProgressBar.visibility = View.GONE

            it?.let { favoriteElastic ->
                val list = favoriteElastic.favoriteTagged
                if (list != null && list.size != 0) {
                    fragmentFavouritesBinding.rlEmpty.visibility = View.GONE
                    fragmentFavouritesBinding.rvFavourite.visibility = View.VISIBLE
                } else {
                    fragmentFavouritesBinding.rlEmpty.visibility = View.VISIBLE
                    fragmentFavouritesBinding.rvFavourite.visibility = View.GONE
                }

                homeViewModel.searchStoreQueryLiveData.observe(viewLifecycleOwner, {
                    val storeInfoViewAllAdapter = StoreInfoViewAllAdapter(list, requireContext(), this, it.latitude.toDouble(), it.longitude.toDouble(), true)
                    fragmentFavouritesBinding.rvFavourite.adapter = storeInfoViewAllAdapter
                })

                AppUtils.saveFavouriteCodeQRs(list)
            }
        })

    }

    private fun setUpRecyclerView() {
        fragmentFavouritesBinding.rvFavourite.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        fragmentFavouritesBinding.rvFavourite.layoutManager = layoutManager
        fragmentFavouritesBinding.rvFavourite.itemAnimator = DefaultItemAnimator()
    }

    override fun onStoreItemClick(item: BizStoreElastic) {
        val intent: Intent
        val b = Bundle()
        when (item.businessType) {
            BusinessTypeEnum.DO, BusinessTypeEnum.CD, BusinessTypeEnum.CDQ, BusinessTypeEnum.BK -> {
                // open hospital profile
                intent = Intent(requireContext(), BeforeJoinActivity::class.java)
                intent.putExtra(IBConstant.KEY_IS_DO, item.businessType == BusinessTypeEnum.DO)
                intent.putExtra(IBConstant.KEY_CODE_QR, item.codeQR)
                intent.putExtra(IBConstant.KEY_FROM_LIST, false)
                intent.putExtra(IBConstant.KEY_IS_CATEGORY, false)
                intent.putExtra(IBConstant.KEY_IMAGE_URL, AppUtils.getImageUrls(BuildConfig.PROFILE_BUCKET, item.displayImage))
                startActivity(intent)
            }
            BusinessTypeEnum.PH -> {
                // open order screen
                intent = Intent(requireContext(), StoreDetailActivity::class.java)
                b.putSerializable("BizStoreElastic", item)
                intent.putExtras(b)
                startActivity(intent)
            }
            BusinessTypeEnum.RSQ, BusinessTypeEnum.GSQ, BusinessTypeEnum.BAQ, BusinessTypeEnum.CFQ, BusinessTypeEnum.FTQ, BusinessTypeEnum.STQ, BusinessTypeEnum.HS, BusinessTypeEnum.PW ->                 //@TODO Modification done due to corona crisis, Re-check all the functionality
                //proper testing required
                if (BusinessSupportEnum.OQ == item.businessType.businessSupport) {
                    intent = Intent(requireContext(), BeforeJoinOrderQueueActivity::class.java)
                    b.putString(IBConstant.KEY_CODE_QR, item.codeQR)
                    b.putBoolean(IBConstant.KEY_FROM_LIST, false)
                    b.putBoolean(IBConstant.KEY_IS_CATEGORY, false)
                    b.putSerializable("BizStoreElastic", item)
                    intent.putExtras(b)
                    startActivity(intent)
                } else {
                    Log.d(TAG, "Reached un-supported condition")
                    FirebaseCrashlytics.getInstance().log("Reached un-supported condition " + item.businessType)
                }
            else -> {
                // open order screen
                intent = Intent(requireContext(), StoreWithMenuActivity::class.java)
                b.putSerializable("BizStoreElastic", item)
                intent.putExtras(b)
                startActivity(intent)
            }
        }
    }

    override fun responseErrorPresenter(eej: ErrorEncounteredJson?) {
        super.responseErrorPresenter(eej)
        progressLoaderBinding.clProgressBar.visibility = View.GONE
        fragmentFavouritesBinding.rlEmpty.visibility = View.VISIBLE
        fragmentFavouritesBinding.rvFavourite.visibility = View.GONE
    }

}