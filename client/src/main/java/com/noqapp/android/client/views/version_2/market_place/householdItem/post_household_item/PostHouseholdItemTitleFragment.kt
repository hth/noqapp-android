package com.noqapp.android.client.views.version_2.market_place.householdItem.post_household_item

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.noqapp.android.client.databinding.FragmentPostHouseHoldItemTitleBinding
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.market_place.householdItem.HouseholdItemViewModel
import com.noqapp.android.common.model.types.category.HouseholdItemCategoryEnum
import com.noqapp.android.common.model.types.category.ItemConditionEnum
import com.noqapp.android.common.pojos.HouseHoldItemEntity

class PostHouseHoldItemTitleFragment : BaseFragment() {
    private lateinit var fragmentPostHouseHoldItemTitleBinding: FragmentPostHouseHoldItemTitleBinding
    private lateinit var householdItemViewModel: HouseholdItemViewModel
    private lateinit var postHouseHoldTitleFragmentInteractionListener: PostHouseHoldTitleFragmentInteractionListener
    private var savedHouseHoldItemEntityInstance: HouseHoldItemEntity? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PostHouseHoldTitleFragmentInteractionListener) {
            postHouseHoldTitleFragmentInteractionListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPostHouseHoldItemTitleBinding = FragmentPostHouseHoldItemTitleBinding.inflate(inflater, container, false)
        householdItemViewModel = ViewModelProvider(requireActivity())[HouseholdItemViewModel::class.java]
        return fragmentPostHouseHoldItemTitleBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }
        observeData()
        setListeners()
    }

    private fun observeData() {
        householdItemViewModel.getHouseHoldItem(requireContext())
            .observe(viewLifecycleOwner, {
                if (it.isNotEmpty()) {
                    val houseHoldItemEntity = it[0]
                    fragmentPostHouseHoldItemTitleBinding.etTitle.setText(houseHoldItemEntity.title)
                    fragmentPostHouseHoldItemTitleBinding.etDescription.setText(houseHoldItemEntity.description)
                    savedHouseHoldItemEntityInstance = houseHoldItemEntity
                }
            })
    }

    private fun setListeners() {
        fragmentPostHouseHoldItemTitleBinding.cvNext.setOnClickListener {
            if (validate()) {
                insertPropertyRentalInDb()
                postHouseHoldTitleFragmentInteractionListener.goToPostHouseHoldItemDetails()
            }
        }

        fragmentPostHouseHoldItemTitleBinding.tvNext.setOnClickListener {
            if (validate()) {
                insertPropertyRentalInDb()
                postHouseHoldTitleFragmentInteractionListener.goToPostHouseHoldItemDetails()
            }
        }
    }

    private fun insertPropertyRentalInDb() {
        if (null == savedHouseHoldItemEntityInstance) {
            savedHouseHoldItemEntityInstance = HouseHoldItemEntity(
                1,
                ItemConditionEnum.B.description,
                listOf(0.0, 0.0),
                0,
                fragmentPostHouseHoldItemTitleBinding.etTitle.text.toString(),
                fragmentPostHouseHoldItemTitleBinding.etDescription.text.toString(),
                null,
                null,
                null,
                null,
                null,
                listOf()
            )
        } else {
            savedHouseHoldItemEntityInstance!!.title = fragmentPostHouseHoldItemTitleBinding.etTitle.text.toString()
            savedHouseHoldItemEntityInstance!!.description = fragmentPostHouseHoldItemTitleBinding.etDescription.text.toString()
        }

        householdItemViewModel.insertHouseHoldItem(requireContext(), savedHouseHoldItemEntityInstance)
    }

    private fun validate(): Boolean {
        if (fragmentPostHouseHoldItemTitleBinding.etTitle.text.isNullOrEmpty()) {
            showSnackBar("Please enter title.")
            return false
        } else if (fragmentPostHouseHoldItemTitleBinding.etDescription.text.isNullOrEmpty()) {
            showSnackBar("Please enter property description.")
            return false
        }
        return true
    }


    private fun showSnackBar(text: String) {
        Snackbar.make(
            fragmentPostHouseHoldItemTitleBinding.root, text,
            Snackbar.LENGTH_SHORT
        ).show()
    }
}

interface PostHouseHoldTitleFragmentInteractionListener {
    fun goToPostHouseHoldItemDetails()
}
