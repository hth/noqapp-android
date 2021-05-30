package com.noqapp.android.client.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.LayoutTokenBinding
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue

class TokenAndQueueAdapter(private val context: Context, private val tokenAndQueueList: MutableList<JsonTokenAndQueue>) : RecyclerView.Adapter<TokenAndQueueAdapter.TokenAndQueueViewHolder>() {

    inner class TokenAndQueueViewHolder(private val layoutTokenBinding: LayoutTokenBinding) : RecyclerView.ViewHolder(layoutTokenBinding.root) {
        fun bind(tokenAndQueue: JsonTokenAndQueue) {
            layoutTokenBinding.tvTokenNumber.text = tokenAndQueue.displayServingNumber
            layoutTokenBinding.tvTimeSlot.text = context.getString(R.string.txt_time_slot, tokenAndQueue.startHour.toString(), tokenAndQueue.endHour.toString())
            layoutTokenBinding.tvTokenName.text = tokenAndQueue.displayName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TokenAndQueueViewHolder {
        val viewBinding = LayoutTokenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TokenAndQueueViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return tokenAndQueueList.size
    }

    override fun onBindViewHolder(holder: TokenAndQueueViewHolder, position: Int) {
        holder.bind(tokenAndQueueList[position])
    }

    fun addItems(tokenAndQueueListItems: List<JsonTokenAndQueue>) {
        tokenAndQueueList.clear()
        tokenAndQueueList.addAll(tokenAndQueueListItems)
        notifyDataSetChanged()
    }
}