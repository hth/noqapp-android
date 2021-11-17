package com.noqapp.android.client.views.version_2.market_place.householdItem.post_household_item

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.FragmentPostHouseHoldItemReviewBinding
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.market_place.householdItem.HouseholdItemViewModel
import com.noqapp.android.common.model.types.category.ItemConditionEnum
import com.noqapp.android.common.pojos.HouseHoldItemEntity
import com.squareup.okhttp.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class PostHouseholdItemReviewFragment : BaseFragment() {

    private lateinit var fragmentPostHouseHoldItemReviewBinding: FragmentPostHouseHoldItemReviewBinding
    private lateinit var householdItemViewModel: HouseholdItemViewModel
    private lateinit var propertyRentalImageAdapter: PropertyRentalImageAdapter

    private var houseHoldItemEntity: HouseHoldItemEntity? = null
    private var imageUploadCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPostHouseHoldItemReviewBinding =
            FragmentPostHouseHoldItemReviewBinding.inflate(inflater, container, false)
        householdItemViewModel =
            ViewModelProvider(requireActivity())[HouseholdItemViewModel::class.java]
        return fragmentPostHouseHoldItemReviewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }
        setUpRecyclerView()
        observeData()
        setListeners()
    }

    private fun setUpRecyclerView() {
        propertyRentalImageAdapter = PropertyRentalImageAdapter(mutableListOf()) {}
        fragmentPostHouseHoldItemReviewBinding.rvSelectedImages.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        fragmentPostHouseHoldItemReviewBinding.rvSelectedImages.setHasFixedSize(true)
        fragmentPostHouseHoldItemReviewBinding.rvSelectedImages.adapter = propertyRentalImageAdapter
    }

    private fun setListeners() {
        fragmentPostHouseHoldItemReviewBinding.tvEdit.setOnClickListener {
            findNavController().navigate(R.id.action_review_house_hold_post_from_start)
        }

        fragmentPostHouseHoldItemReviewBinding.cvProceed.setOnClickListener {
            houseHoldItemEntity?.let { pre ->
                fragmentPostHouseHoldItemReviewBinding.clProgressBar.visibility = View.VISIBLE
                householdItemViewModel.postMarketPlace(
                    pre.price,
                    pre.title,
                    pre.description,
                    pre.town,
                    pre.city,
                    pre.address,
                    pre.landmark,
                    pre.coordinates[1],
                    pre.coordinates[0],
                    ItemConditionEnum.getNameByDescription(pre.itemConditionType)
                )
            }
        }
    }

    private fun observeData() {
        householdItemViewModel.getHouseHoldItem(requireContext())
            .observe(viewLifecycleOwner, {
                if (it.isNotEmpty()) {
                    houseHoldItemEntity = it[0]
                    fragmentPostHouseHoldItemReviewBinding.tvRentPerMonth.text =
                        houseHoldItemEntity?.price.toString()
                    fragmentPostHouseHoldItemReviewBinding.tvRentalType.text =
                        houseHoldItemEntity?.itemConditionType
                    fragmentPostHouseHoldItemReviewBinding.tvTownLocality.text =
                        houseHoldItemEntity?.town
                    fragmentPostHouseHoldItemReviewBinding.tvCityArea.text = houseHoldItemEntity?.city
                    fragmentPostHouseHoldItemReviewBinding.tvLandmark.text =
                        houseHoldItemEntity?.landmark
                    fragmentPostHouseHoldItemReviewBinding.tvRentalAddress.text =
                        houseHoldItemEntity?.address

                    propertyRentalImageAdapter.addAllImages(houseHoldItemEntity?.images)
                }
            })

        householdItemViewModel.postMarketPlaceJsonLiveData.observe(
            viewLifecycleOwner,
            { mre ->
                houseHoldItemEntity?.let { pre ->
                    pre.images?.forEach {
                        uploadImage(it, mre.id)
                    }
                }
            })

        householdItemViewModel.postImagesLiveData.observe(viewLifecycleOwner, {
            imageUploadCount = imageUploadCount.inc()
            houseHoldItemEntity?.let { pre ->
                if (imageUploadCount == pre.images?.size) {
                    fragmentPostHouseHoldItemReviewBinding.clProgressBar.visibility = View.GONE
                    householdItemViewModel.deletePostsLocally(requireContext())
                    activity?.finish()
                }
            }
        })
    }

    private fun uploadImage(imagePath: String, marketPlaceId: String) {
        getMimeType(Uri.parse(imagePath))?.let { type ->
            val file = File(imagePath)
            val marketplaceImage = MultipartBody.Part.createFormData(
                "file",
                file.name,
                file.asRequestBody(MediaType.parse(type).toString().toMediaType())
            )
            val postId = RequestBody.create("text/plain".toMediaType(), marketPlaceId)
            householdItemViewModel.postImages(marketplaceImage, postId)
        }
    }

    private fun getMimeType(uri: Uri): String? {
        var mimeType: String? = null
        mimeType = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cr = requireActivity().contentResolver
            cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase())
        }
        return mimeType
    }

}