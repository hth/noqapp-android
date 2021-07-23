package com.noqapp.android.client.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noqapp.android.client.databinding.LayoutScheduledAppointmentBinding
import com.noqapp.android.client.utils.AppUtils
import com.noqapp.android.common.beans.JsonSchedule
import com.noqapp.android.common.model.types.BusinessTypeEnum
import com.noqapp.android.common.model.types.category.CanteenStoreDepartmentEnum
import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum
import com.noqapp.android.common.utils.CommonHelper

/** Appointment display on home screen. */
class ScheduledAppointmentAdapter(
        private val context: Context,
        private val jsonScheduleList: MutableList<JsonSchedule>,
        val onItemClick: (JsonSchedule) -> Unit
) : RecyclerView.Adapter<ScheduledAppointmentAdapter.ScheduledAppointmentViewHolder>() {

    inner class ScheduledAppointmentViewHolder(private val layoutScheduledAppointmentBinding: LayoutScheduledAppointmentBinding) : RecyclerView.ViewHolder(layoutScheduledAppointmentBinding.root) {
        fun bind(jsonSchedule: JsonSchedule) {
            layoutScheduledAppointmentBinding.tvTitle.text = jsonSchedule.jsonQueueDisplay.displayName
            when (jsonSchedule.jsonQueueDisplay.businessType) {
                BusinessTypeEnum.DO -> layoutScheduledAppointmentBinding.tvDegree.text = MedicalDepartmentEnum.valueOf(jsonSchedule.jsonQueueDisplay.bizCategoryId).description
                BusinessTypeEnum.CDQ -> layoutScheduledAppointmentBinding.tvDegree.text = CanteenStoreDepartmentEnum.valueOf(jsonSchedule.jsonQueueDisplay.bizCategoryId).description
                else -> layoutScheduledAppointmentBinding.tvDegree.text = jsonSchedule.jsonQueueDisplay.businessName
            }
            layoutScheduledAppointmentBinding.tvStoreAddress.text = AppUtils.getStoreAddress(jsonSchedule.jsonQueueDisplay.town, jsonSchedule.jsonQueueDisplay.area)
            layoutScheduledAppointmentBinding.tvScheduleTime.text = jsonSchedule.appointmentState.description
            try {
                val scheduleDate = CommonHelper.SDF_YYYY_MM_DD.parse(jsonSchedule.scheduleDate);
                val date = CommonHelper.SDF_DOB_FROM_UI.format(scheduleDate)
                layoutScheduledAppointmentBinding.tvScheduleDate.text = date
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            layoutScheduledAppointmentBinding.root.setOnClickListener { onItemClick(jsonSchedule) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduledAppointmentViewHolder {
        val viewBinding = LayoutScheduledAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduledAppointmentViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return jsonScheduleList.size
    }

    override fun onBindViewHolder(holder: ScheduledAppointmentViewHolder, position: Int) {
        holder.bind(jsonScheduleList[position])
    }

    fun addItems(tokenAndQueueListItems: List<JsonSchedule>) {
        jsonScheduleList.clear()
        jsonScheduleList.addAll(tokenAndQueueListItems)
        notifyDataSetChanged()
    }
}
