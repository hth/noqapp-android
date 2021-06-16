package com.noqapp.android.client.views.adapters

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.LayoutTokenBinding
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.utils.TokenStatusUtils
import com.noqapp.android.common.model.types.BusinessTypeEnum
import com.noqapp.android.common.model.types.QueueOrderTypeEnum
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum

class TokenAndQueueAdapter(
    private val context: Context,
    private val tokenAndQueueList: MutableList<JsonTokenAndQueue>,
    val onItemClick: (JsonTokenAndQueue) -> Unit
) : RecyclerView.Adapter<TokenAndQueueAdapter.TokenAndQueueViewHolder>() {

    inner class TokenAndQueueViewHolder(private val layoutTokenBinding: LayoutTokenBinding) :
        RecyclerView.ViewHolder(layoutTokenBinding.root) {
        fun bind(tokenAndQueue: JsonTokenAndQueue) {

            layoutTokenBinding.root.setOnClickListener {
                onItemClick(tokenAndQueue)
            }

            layoutTokenBinding.tvTokenNumber.text = tokenAndQueue.displayServingNumber
            layoutTokenBinding.tvTokenName.text = tokenAndQueue.displayName

            if (tokenAndQueue.businessType.queueOrderType == QueueOrderTypeEnum.Q) {
                layoutTokenBinding.tvToken.text = context.getString(R.string.token)
                when {
                    tokenAndQueue.token <= tokenAndQueue.servingNumber -> {
                        layoutTokenBinding.tvQueueStatus.text =
                            context.getString(R.string.your_turn)
                    }
                    tokenAndQueue.servingNumber == 0 -> {
                        layoutTokenBinding.tvQueueStatus.text =
                            context.getString(R.string.queue_not_started)
                        when (tokenAndQueue.businessType) {
                            BusinessTypeEnum.CD, BusinessTypeEnum.CDQ -> {
                                layoutTokenBinding.tvTimeSlot.text = String.format(
                                    context.getString(R.string.time_slot_formatted),
                                    tokenAndQueue.timeSlotMessage
                                )
                            }
                            else -> {
                                val waitTime = displayWaitTimes(tokenAndQueue)
                                layoutTokenBinding.tvTimeSlot.text = String.format(
                                    context.getString(R.string.time_slot_formatted),
                                    waitTime
                                )
                            }
                        }
                    }
                    else -> {
                        layoutTokenBinding.tvQueueStatus.text =
                            context.getString(R.string.position_in_queue_label) + " " + tokenAndQueue.afterHowLongForDisplay()
                        when (tokenAndQueue.businessType) {
                            BusinessTypeEnum.CD, BusinessTypeEnum.CDQ -> {
                                layoutTokenBinding.tvTimeSlot.text = String.format(
                                    context.getString(R.string.time_slot_formatted),
                                    tokenAndQueue.timeSlotMessage
                                )
                            }
                            else -> {
                                val waitTime = displayWaitTimes(tokenAndQueue)
                                layoutTokenBinding.tvTimeSlot.text = String.format(
                                    context.getString(R.string.time_slot_formatted),
                                    waitTime
                                )
                            }
                        }
                    }
                }
            } else if (tokenAndQueue.businessType.queueOrderType == QueueOrderTypeEnum.O) {
                layoutTokenBinding.tvToken.text = context.getString(R.string.order)
                when {
                    tokenAndQueue.token - tokenAndQueue.servingNumber <= 0 -> {
                        when (tokenAndQueue.purchaseOrderState) {
                            PurchaseOrderStateEnum.OP -> layoutTokenBinding.tvQueueStatus.text =
                                context.getString(R.string.txt_order_being_prepared)
                            else -> layoutTokenBinding.tvQueueStatus.text =
                                tokenAndQueue.purchaseOrderState.friendlyDescription
                        }
                    }
                    tokenAndQueue.servingNumber == 0 -> {
                        layoutTokenBinding.tvQueueStatus.text =
                            context.getString(R.string.queue_not_started)
                    }
                    else -> {
                        layoutTokenBinding.tvQueueStatus.text =
                            context.getString(R.string.serving_now) + " " + tokenAndQueue.afterHowLongForDisplay()
                    }
                }
            }
        }

        // Display wait time
        private fun displayWaitTimes(jsonTokenAndQueue: JsonTokenAndQueue): String? {
            try {
                var avgServiceTime = jsonTokenAndQueue.averageServiceTime
                if (avgServiceTime == 0L) {
                    val prefs: SharedPreferences =
                        context.getSharedPreferences(Constants.APP_PACKAGE, Context.MODE_PRIVATE)
                    avgServiceTime = prefs.getLong(
                        String.format(
                            Constants.ESTIMATED_WAIT_TIME_PREF_KEY,
                            jsonTokenAndQueue.codeQR
                        ), 0
                    )
                }
                return TokenStatusUtils.calculateEstimatedWaitTime(
                    avgServiceTime,
                    jsonTokenAndQueue.afterHowLong(),
                    jsonTokenAndQueue.queueStatus,
                    jsonTokenAndQueue.startHour,
                    context
                )
            } catch (e: Exception) {
                Log.e(
                    TokenAndQueueAdapter::class.java.simpleName,
                    "Error setting wait time reason: " + e.localizedMessage,
                    e
                )
            }
            return null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TokenAndQueueViewHolder {
        val viewBinding =
            LayoutTokenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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