package com.noqapp.android.client.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.AddressListItemBinding
import com.noqapp.android.client.utils.ShowCustomDialog
import com.noqapp.android.common.beans.JsonUserAddress

class AddressListAdapter(private val addressList: MutableList<JsonUserAddress>, val clickListener: (JsonUserAddress, View) -> Unit) : RecyclerView.Adapter<AddressListAdapter.AddressListViewHolder>() {

    inner class AddressListViewHolder(private val addressListItemView: View) : RecyclerView.ViewHolder(addressListItemView) {
        private val tvPrimary: TextView
        private val tvAddress: TextView
        private val tvArea: TextView
        private val ivDelete: AppCompatImageView
        private val viewDivider: View

        init {
            val binding = AddressListItemBinding.bind(addressListItemView)
            tvPrimary = binding.tvPrimary
            tvAddress = binding.tvAddress
            tvArea = binding.tvArea
            ivDelete = binding.ivDelete
            viewDivider = binding.viewDivider
        }

        fun bind(jsonUserAddress: JsonUserAddress, position: Int) {
            tvArea.text = jsonUserAddress.area
            tvAddress.text = jsonUserAddress.address

            if (jsonUserAddress.isPrimaryAddress) {
                tvPrimary.visibility = View.VISIBLE
            } else {
                tvPrimary.visibility = View.GONE
            }

            if (position == addressList.size - 1) {
                viewDivider.visibility = View.GONE
            } else {
                viewDivider.visibility = View.VISIBLE
            }

            ivDelete.setOnClickListener {
                val showDialog = ShowCustomDialog(it.context, true)
                showDialog.setDialogClickListener(object : ShowCustomDialog.DialogClickListener {
                    override fun btnPositiveClick() {
                        clickListener(jsonUserAddress, it)
                    }

                    override fun btnNegativeClick() {
                        //Do nothing
                    }
                })
                showDialog.displayDialog("Delete Address", "Do you want to delete address from address list?")
            }

            addressListItemView.setOnClickListener {
                clickListener(jsonUserAddress, it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.address_list_item, parent, false)
        return AddressListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    override fun onBindViewHolder(holder: AddressListViewHolder, position: Int) {
        holder.bind(addressList[position], position)
    }

    fun addItems(jsonUserAddressList: List<JsonUserAddress>) {
        addressList.clear()
        addressList.addAll(jsonUserAddressList)
        notifyDataSetChanged()
    }
}
