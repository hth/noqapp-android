package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.common.beans.JsonProfile;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter {
    private final OnItemClickListener listener;
    private List<JsonProfile> jsonProfiles;
    private Context context;

    public ProfileAdapter(Context context, List<JsonProfile> jsonProfiles,
                          OnItemClickListener listener) {
        this.jsonProfiles = jsonProfiles;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rcv_item_user_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder Vholder, final int position) {
        ViewHolder holder = (ViewHolder) Vholder;
        final JsonProfile jsonProfile = jsonProfiles.get(position);
        holder.tv_name.setText(jsonProfile.getName());
        AppUtilities.loadProfilePic(holder.iv_profile, jsonProfile.getProfileImage(), context);
        holder.btn_view_profile.setOnClickListener((View v) -> {
            if (null != listener)
                listener.onProfileItemClick(jsonProfile);
        });
        holder.btn_hospital_visit.setOnClickListener((View v) -> {
            if (null != listener)
                listener.onHospitalVisitClick(jsonProfile);
        });
    }

    @Override
    public int getItemCount() {
        return jsonProfiles.size();
    }


    public interface OnItemClickListener {
        void onProfileItemClick(JsonProfile jsonProfile);

        void onHospitalVisitClick(JsonProfile jsonProfile);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private ImageView iv_profile;
        private Button btn_hospital_visit;
        private Button btn_view_profile;
        private CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.iv_profile = itemView.findViewById(R.id.iv_profile);
            this.btn_hospital_visit = itemView.findViewById(R.id.btn_hospital_visit);
            this.btn_view_profile = itemView.findViewById(R.id.btn_view_profile);
            this.cardview = itemView.findViewById(R.id.cardview);
        }
    }
}