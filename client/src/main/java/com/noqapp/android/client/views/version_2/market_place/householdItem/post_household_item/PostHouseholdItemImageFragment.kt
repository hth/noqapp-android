package com.noqapp.android.client.views.version_2.market_place.householdItem.post_household_item

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.noqapp.android.client.databinding.FragmentUploadPropertyRentalImagesBinding
import com.noqapp.android.client.views.customviews.RecyclerViewItemDecorator
import com.noqapp.android.client.views.version_2.market_place.householdItem.HouseholdItemViewModel
import com.noqapp.android.client.views.version_2.market_place.householdItem.ImageUploadFragment
import com.noqapp.android.common.pojos.HouseHoldItemEntity

class PostHouseHoldItemImageFragment : ImageUploadFragment() {

    private lateinit var householdItemViewModel: HouseholdItemViewModel
    private lateinit var propertyRentalImageAdapter: PropertyRentalImageAdapter
    private lateinit var houseHoldItemEntity: HouseHoldItemEntity

    private lateinit var postHouseHoldItemImageFragmentInteractionListener: PostHouseHoldItemImageFragmentInteractionListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PostHouseHoldItemImageFragmentInteractionListener) {
            postHouseHoldItemImageFragmentInteractionListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPostPropertyRentalUpload =
            FragmentUploadPropertyRentalImagesBinding.inflate(inflater, container, false)
        householdItemViewModel =
            ViewModelProvider(requireActivity())[HouseholdItemViewModel::class.java]
        return fragmentPostPropertyRentalUpload.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        setUpRecyclerView()
        observeData()

        setListeners()
    }

    private fun observeData() {
        householdItemViewModel.getHouseHoldItem(requireContext())
            .observe(viewLifecycleOwner, {
                if (it.isNotEmpty()) {
                    houseHoldItemEntity = it[0]
                    propertyRentalImageAdapter.addAllImages(houseHoldItemEntity.images)
                }
            })
    }

    private fun setUpRecyclerView() {
        propertyRentalImageAdapter = PropertyRentalImageAdapter(mutableListOf()) {
            houseHoldItemEntity.images.forEach { imagePath ->
                if (it == imagePath) {
                    val list = houseHoldItemEntity.images.toMutableList()
                    list.remove(imagePath)
                    houseHoldItemEntity.images = list
                }
            }
            householdItemViewModel.insertHouseHoldItem(requireContext(), houseHoldItemEntity)
        }
        fragmentPostPropertyRentalUpload.rvSelectedImages.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        fragmentPostPropertyRentalUpload.rvSelectedImages.setHasFixedSize(true)
        fragmentPostPropertyRentalUpload.rvSelectedImages.addItemDecoration(
            RecyclerViewItemDecorator(10)
        )
        fragmentPostPropertyRentalUpload.rvSelectedImages.adapter = propertyRentalImageAdapter
    }

    private fun setListeners() {
        fragmentPostPropertyRentalUpload.cvNext.setOnClickListener {
            if (validate()) {
                houseHoldItemEntity.images = propertyRentalImageAdapter.getAllImages()
                householdItemViewModel.insertHouseHoldItem(requireContext(), houseHoldItemEntity)

                postHouseHoldItemImageFragmentInteractionListener.goToPostHouseHoldItemReviewFragment()
            }
        }

        fragmentPostPropertyRentalUpload.ivAddPhoto.setOnClickListener {
            if (checkAndRequestPermissions(requireActivity())) {
                selectImage()
            }
        }
        fragmentPostPropertyRentalUpload.clHead.setOnClickListener {
            if (checkAndRequestPermissions(requireActivity())) {
                selectImage()
            }
        }
    }


    private fun validate(): Boolean {
        if (propertyRentalImageAdapter.getAllImages().isEmpty()) {
            showSnackBar("Please upload atleast one image.")
            return false
        }
        return true
    }

    override fun saveImage(imagePath: String) {
        propertyRentalImageAdapter.addImage(imagePath)
        val images = mutableListOf<String>()
        images.addAll(houseHoldItemEntity.images)
        images.add(imagePath)
        houseHoldItemEntity.images = images
        householdItemViewModel.insertHouseHoldItem(requireContext(), houseHoldItemEntity)
    }

}

interface PostHouseHoldItemImageFragmentInteractionListener {
    fun goToPostHouseHoldItemReviewFragment()
}
