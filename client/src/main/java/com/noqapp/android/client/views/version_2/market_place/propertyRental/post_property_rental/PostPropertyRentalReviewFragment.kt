package com.noqapp.android.client.views.version_2.market_place.propertyRental.post_property_rental

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
import com.noqapp.android.client.databinding.FragmentPostPropertyRentalReviewBinding
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.market_place.propertyRental.PropertyRentalViewModel
import com.noqapp.android.common.model.types.category.RentalTypeEnum
import com.noqapp.android.common.pojos.PropertyRentalEntity
import com.squareup.okhttp.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

class PostPropertyRentalReviewFragment : BaseFragment() {

    private lateinit var fragmentPostPropertyRentalReview: FragmentPostPropertyRentalReviewBinding
    private lateinit var propertyRentalViewModel: PropertyRentalViewModel
    private lateinit var propertyRentalImageAdapter: PropertyRentalImageAdapter

    private var propertyRentalEntity: PropertyRentalEntity? = null
    private var imageUploadCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPostPropertyRentalReview = FragmentPostPropertyRentalReviewBinding.inflate(inflater, container, false)
        propertyRentalViewModel = ViewModelProvider(requireActivity())[PropertyRentalViewModel::class.java]
        return fragmentPostPropertyRentalReview.root
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
        propertyRentalImageAdapter.isClickEnable = false
        fragmentPostPropertyRentalReview.rvSelectedImages.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        fragmentPostPropertyRentalReview.rvSelectedImages.setHasFixedSize(true)
        fragmentPostPropertyRentalReview.rvSelectedImages.adapter = propertyRentalImageAdapter
    }

    private fun setListeners() {
        fragmentPostPropertyRentalReview.tvEdit.setOnClickListener {
            findNavController().navigate(R.id.action_review_to_start_destination)
        }

        fragmentPostPropertyRentalReview.cvProceed.setOnClickListener {
            propertyRentalEntity?.let { pre ->
                fragmentPostPropertyRentalReview.clProgressBar.visibility = View.VISIBLE
                propertyRentalViewModel.postMarketPlace(
                    pre.price,
                    pre.title,
                    pre.description,
                    pre.bathroom,
                    pre.bedroom,
                    pre.carpetArea,
                    pre.town,
                    pre.city,
                    pre.address,
                    pre.landmark,
                    pre.availableFrom,
                    RentalTypeEnum.getNameByDescription(pre.rentalType),
                    pre.coordinates[1],
                    pre.coordinates[0]
                )
            }
        }
    }

    private fun observeData() {
        propertyRentalViewModel.getPropertyRental(requireContext())
            .observe(viewLifecycleOwner, {
                if (it.isNotEmpty()) {
                    propertyRentalEntity = it[0]
                    fragmentPostPropertyRentalReview.tvAvailableFrom.text = propertyRentalEntity?.availableFrom
                    fragmentPostPropertyRentalReview.tvBathrooms.text = propertyRentalEntity?.bathroom.toString()
                    fragmentPostPropertyRentalReview.tvBedrooms.text = propertyRentalEntity?.bedroom.toString()
                    fragmentPostPropertyRentalReview.tvCarpetArea.text = propertyRentalEntity?.carpetArea.toString()
                    fragmentPostPropertyRentalReview.tvPrice.text = propertyRentalEntity?.price.toString()
                    fragmentPostPropertyRentalReview.tvRentalType.text = propertyRentalEntity?.rentalType
                    fragmentPostPropertyRentalReview.tvTownLocality.text = propertyRentalEntity?.town
                    fragmentPostPropertyRentalReview.tvCityArea.text = propertyRentalEntity?.city
                    fragmentPostPropertyRentalReview.tvLandmark.text = propertyRentalEntity?.landmark
                    fragmentPostPropertyRentalReview.tvRentalAddress.text = propertyRentalEntity?.address

                    propertyRentalImageAdapter.addAllImages(propertyRentalEntity?.images)
                }
            })

        propertyRentalViewModel.postMarketPlaceJsonLiveData.observe(
            viewLifecycleOwner,
            { mre ->
                propertyRentalEntity?.let { pre ->
                    pre.images?.forEach {
                        uploadImage(it, mre.id)
                    }
                }
            })

        propertyRentalViewModel.postImagesLiveData.observe(viewLifecycleOwner, {
            imageUploadCount = imageUploadCount.inc()
            propertyRentalEntity?.let { pre ->
                if (imageUploadCount == pre.images?.size) {
                    fragmentPostPropertyRentalReview.clProgressBar.visibility = View.GONE
                    propertyRentalViewModel.deletePostsLocally(requireContext())
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
            propertyRentalViewModel.postImages(marketplaceImage, postId)
        }
    }

    private fun getMimeType(uri: Uri): String? {
        var mimeType: String? = null
        mimeType = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cr = requireActivity().contentResolver
            cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase(Locale.getDefault()))
        }
        return mimeType
    }
}
