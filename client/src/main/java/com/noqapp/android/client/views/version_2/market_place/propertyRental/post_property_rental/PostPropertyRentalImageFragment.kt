package com.noqapp.android.client.views.version_2.market_place.propertyRental.post_property_rental

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.noqapp.android.client.databinding.FragmentUploadPropertyRentalImagesBinding
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.views.version_2.market_place.householdItem.ImageUploadFragment
import com.noqapp.android.client.views.version_2.market_place.propertyRental.PropertyRentalViewModel
import com.noqapp.android.common.pojos.PropertyRentalEntity

class PostPropertyRentalImageFragment : ImageUploadFragment() {
    private lateinit var propertyRentalViewModel: PropertyRentalViewModel
    private lateinit var propertyRentalImageAdapter: PropertyRentalImageAdapter
    private lateinit var propertyRentalEntity: PropertyRentalEntity
    private lateinit var postPropertyRentalImageFragmentInteractionListener: PostPropertyRentalImageFragmentInteractionListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PostPropertyRentalImageFragmentInteractionListener) {
            postPropertyRentalImageFragmentInteractionListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPostPropertyRentalUpload = FragmentUploadPropertyRentalImagesBinding.inflate(inflater, container, false)
        propertyRentalViewModel = ViewModelProvider(requireActivity())[PropertyRentalViewModel::class.java]
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
        propertyRentalViewModel.getPropertyRental(requireContext())
            .observe(viewLifecycleOwner, {
                if (it.isNotEmpty()) {
                    propertyRentalEntity = it[0]
                    propertyRentalImageAdapter.addAllImages(propertyRentalEntity.images)
                }
            })
    }

    private fun setUpRecyclerView() {
        propertyRentalImageAdapter = PropertyRentalImageAdapter(mutableListOf()) {
            propertyRentalEntity.images.forEach { imagePath ->
                if (it == imagePath) {
                    val list = propertyRentalEntity.images.toMutableList()
                    list.remove(imagePath)
                    propertyRentalEntity.images = list
                }
            }
            propertyRentalViewModel.insertPropertyRental(requireContext(), propertyRentalEntity)
        }
        fragmentPostPropertyRentalUpload.rvSelectedImages.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        fragmentPostPropertyRentalUpload.rvSelectedImages.setHasFixedSize(true)
        fragmentPostPropertyRentalUpload.rvSelectedImages.adapter = propertyRentalImageAdapter
    }

    private fun setListeners() {
        fragmentPostPropertyRentalUpload.cvNext.setOnClickListener {
            if(validate()) {
                propertyRentalEntity.images = propertyRentalImageAdapter.getAllImages()
                propertyRentalViewModel.insertPropertyRental(requireContext(), propertyRentalEntity)

                postPropertyRentalImageFragmentInteractionListener.goToPostPropertyRentalReviewFragment()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.REQUEST_ID_MULTIPLE_PERMISSIONS -> if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    requireContext(),
                    "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT
                )
                    .show()
            } else if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    requireContext(),
                    "FlagUp Requires Access to Your Storage.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
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
        images.addAll(propertyRentalEntity.images)
        images.add(imagePath)
        propertyRentalEntity.images = images
        propertyRentalViewModel.insertPropertyRental(requireContext(), propertyRentalEntity)

    }
}

interface PostPropertyRentalImageFragmentInteractionListener {
    fun goToPostPropertyRentalReviewFragment()
}
