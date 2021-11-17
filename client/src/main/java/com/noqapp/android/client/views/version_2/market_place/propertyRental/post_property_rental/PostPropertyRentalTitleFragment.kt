package com.noqapp.android.client.views.version_2.market_place.propertyRental.post_property_rental

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.noqapp.android.client.databinding.FragmentPostPropertyRentalTitleBinding
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.market_place.propertyRental.PostPropertyRentalViewModel
import com.noqapp.android.common.model.types.category.RentalTypeEnum
import com.noqapp.android.common.pojos.PropertyRentalEntity

class PostPropertyRentalTitleFragment : BaseFragment() {

    private lateinit var fragmentPostPropertyRentalTitle: FragmentPostPropertyRentalTitleBinding
    private lateinit var postPropertyRentalViewModel: PostPropertyRentalViewModel
    private lateinit var postPropertyRentalTitleFragmentInteractionListener: PostPropertyRentalTitleFragmentInteractionListener
    private var savedPropertyRentalEntityInstance: PropertyRentalEntity? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PostPropertyRentalTitleFragmentInteractionListener)
            postPropertyRentalTitleFragmentInteractionListener = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPostPropertyRentalTitle =
            FragmentPostPropertyRentalTitleBinding.inflate(inflater, container, false)
        postPropertyRentalViewModel =
            ViewModelProvider(requireActivity())[PostPropertyRentalViewModel::class.java]
        return fragmentPostPropertyRentalTitle.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }
        observeData()
        setListeners()
    }

    private fun observeData() {
        postPropertyRentalViewModel.getPropertyRental(requireContext())
            .observe(viewLifecycleOwner, {
                if (it.isNotEmpty()) {
                    val propertyRentalEntity = it[0]
                    fragmentPostPropertyRentalTitle.etTitle.setText(propertyRentalEntity.title)
                    fragmentPostPropertyRentalTitle.etDescription.setText(propertyRentalEntity.description)
                    savedPropertyRentalEntityInstance = propertyRentalEntity
                }
            })
    }

    private fun setListeners() {
        fragmentPostPropertyRentalTitle.cvNext.setOnClickListener {
            if (validate()) {
                insertPropertyRentalInDb()
                postPropertyRentalTitleFragmentInteractionListener.goToPostPropertyRentalDetails()
            }
        }

        fragmentPostPropertyRentalTitle.tvNext.setOnClickListener {
            if (validate()) {
                insertPropertyRentalInDb()
                postPropertyRentalTitleFragmentInteractionListener.goToPostPropertyRentalDetails()
            }
        }
    }

    private fun insertPropertyRentalInDb() {
        if(null == savedPropertyRentalEntityInstance) {
            savedPropertyRentalEntityInstance = PropertyRentalEntity(
                1,
                0,
                0,
                0,
                RentalTypeEnum.A.description,
                listOf(0.0, 0.0),
                0,
                fragmentPostPropertyRentalTitle.etTitle.text.toString(),
                fragmentPostPropertyRentalTitle.etDescription.text.toString(),
                null,
                null,
                null,
                null,
                null,
                null,
                listOf()
            )
        }else{
            savedPropertyRentalEntityInstance!!.title = fragmentPostPropertyRentalTitle.etTitle.text.toString()
            savedPropertyRentalEntityInstance!!.description =fragmentPostPropertyRentalTitle.etDescription.text.toString()

        }
        postPropertyRentalViewModel.insertPropertyRental(requireContext(), savedPropertyRentalEntityInstance)
    }

    private fun validate(): Boolean {
        if (fragmentPostPropertyRentalTitle.etTitle.text.isNullOrEmpty()) {
            showSnackBar("Please enter title.")
            return false
        } else if (fragmentPostPropertyRentalTitle.etDescription.text.isNullOrEmpty()) {
            showSnackBar("Please enter property description.")
            return false
        }
        return true
    }


    private fun showSnackBar(text: String) {
        Snackbar.make(
            fragmentPostPropertyRentalTitle.root, text,
            Snackbar.LENGTH_SHORT
        ).show()
    }
}

interface PostPropertyRentalTitleFragmentInteractionListener {
    fun goToPostPropertyRentalDetails()
}
